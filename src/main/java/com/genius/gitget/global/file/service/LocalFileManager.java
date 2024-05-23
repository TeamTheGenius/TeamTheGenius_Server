package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_COPIED;
import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_DELETED;
import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_SAVED;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.CopyDTO;
import com.genius.gitget.global.file.dto.FileDTO;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.util.exception.BusinessException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

public class LocalFileManager implements FileManager {
    private final String UPLOAD_PATH;
    private final FileUtil fileUtil;


    public LocalFileManager(FileUtil fileUtil, @Value("${file.upload.path}") String UPLOAD_PATH) {
        this.fileUtil = fileUtil;
        this.UPLOAD_PATH = UPLOAD_PATH;
    }

    @Override
    public FileDTO upload(MultipartFile multipartFile, FileType fileType) {
        fileUtil.validateFile(multipartFile);
        FileDTO fileDTO = fileUtil.getFileDTO(multipartFile, fileType, UPLOAD_PATH);

        try {
            File file = new File(fileDTO.fileURI());
            createPath(fileDTO.fileURI());
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new BusinessException(FILE_NOT_SAVED);
        }

        return fileDTO;
    }

    @Override
    public String getEncodedImage(Files files) {
        try {
            UrlResource urlResource = new UrlResource("file:" + files.getFileURI());
            byte[] encode = Base64.getEncoder().encode(urlResource.getContentAsByteArray());
            return new String(encode, StandardCharsets.UTF_8);
        } catch (IOException e) {
            //TODO: 불러오는 중의 예외에 대해 Logging 추가하기
            return "";
        }
    }

    @Override
    public FileDTO copy(Files files, FileType fileType) {
        CopyDTO copyDTO = fileUtil.getCopyInfo(files, fileType, UPLOAD_PATH);
        createPath(copyDTO.folderURI());

        File originFile = new File(files.getFileURI());
        File copyFile = new File(copyDTO.fileURI());

        try {
            java.nio.file.Files.copy(originFile.toPath(), copyFile.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            throw new BusinessException(FILE_NOT_COPIED);
        }
        return FileDTO.builder()
                .fileType(fileType)
                .originalFilename(copyDTO.originalFilename())
                .savedFilename(copyDTO.savedFilename())
                .fileURI(copyDTO.fileURI())
                .build();
    }

    @Override
    public UpdateDTO update(Files files, MultipartFile multipartFile) {
        deleteInStorage(files);
        FileDTO fileDTO = upload(multipartFile, files.getFileType());

        return UpdateDTO.of(fileDTO);
    }

    @Override
    public void deleteInStorage(Files files) {
        String fileURI = files.getFileURI();
        File targetFile = new File(fileURI);
        if (!targetFile.delete()) {
            throw new BusinessException(FILE_NOT_DELETED);
        }
    }

    private void createPath(String uri) {
        File file = new File(uri);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
