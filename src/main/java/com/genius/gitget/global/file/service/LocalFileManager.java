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
import java.net.MalformedURLException;
import java.nio.file.StandardCopyOption;
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
            fileUtil.createPath(fileDTO.fileURI());
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new BusinessException(FILE_NOT_SAVED);
        }

        return fileDTO;
    }

    @Override
    public UrlResource download(Files files) {
        try {
            return new UrlResource("file:" + files.getFileURI());
        } catch (MalformedURLException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 복사하는 과정 -> topic에서 instance로 복사하고 싶을 수도 있음
     * 1. Files에 기존에 있던 파일을 찾고, savedFilename 및 fileURI 재발급
     * 2. 저장소에 저장
     * 3. 저장한 파일에 관련된 값 반환
     */
    @Override
    public FileDTO copy(Files files, FileType fileType) {
        CopyDTO copyDTO = fileUtil.getCopyInfo(files, fileType, UPLOAD_PATH);
        fileUtil.createPath(copyDTO.folderURI());

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


    /**
     * Update(덮어쓰기)하는 과정
     * 1. Files를 통해 기존에 있던 파일을 찾아서 삭제
     * 2. MultipartFile을 통해 업로드 진행
     * 3. 업로드 된 후, 갱신에 필요한 정보 전달
     */
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
}
