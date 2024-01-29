package com.genius.gitget.global.file.service;

import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.dto.UploadDTO;
import com.genius.gitget.global.file.repository.FilesRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FilesService {
    private final String UPLOAD_PATH;
    private final FilesRepository filesRepository;

    public FilesService(@Value("${file.upload.path}") String UPLOAD_PATH, FilesRepository filesRepository) {
        this.UPLOAD_PATH = UPLOAD_PATH;
        this.filesRepository = filesRepository;
    }

    @Transactional
    public Files uploadFile(MultipartFile receivedFile, String typeStr) throws IOException {
        FileUtil.validateFile(receivedFile);

        UploadDTO uploadDTO = FileUtil.getUploadInfo(receivedFile, typeStr, UPLOAD_PATH);

        saveFile(receivedFile, uploadDTO.fileURI());

        Files file = Files.builder()
                .originalFilename(uploadDTO.originalFilename())
                .savedFilename(uploadDTO.savedFilename())
                .fileType(uploadDTO.fileType())
                .fileURI(uploadDTO.fileURI())
                .build();

        return filesRepository.save(file);
    }

    private void saveFile(MultipartFile receivedFile, String fileURI) throws IOException {
        File targetFile = new File(fileURI);

        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        receivedFile.transferTo(targetFile);
    }

    public FileResponse getEncodedFile(Long fileId) throws IOException {
        return getEncodedFile(filesRepository.findById(fileId));
    }

    public FileResponse getEncodedFile(Optional<Files> files) throws IOException {
        if (files.isEmpty()) {
            return FileResponse.createNotExistFile();
        }

        return FileResponse.createExistFile(files.get());
    }

    public UrlResource getFile(Long fileId) throws MalformedURLException {
        Files files = filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_EXIST));

        return new UrlResource("file:" + files.getFileURI());
    }
}
