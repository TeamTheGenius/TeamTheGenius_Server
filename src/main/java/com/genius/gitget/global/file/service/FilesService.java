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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FilesService {
    private final FileUtil fileUtil;
    private final FilesRepository filesRepository;


    @Transactional
    public FileResponse uploadFile(MultipartFile receivedFile, String typeStr) throws IOException {
        fileUtil.validateFile(receivedFile);

        UploadDTO uploadDTO = fileUtil.getUploadInfo(receivedFile, typeStr);

        saveFile(receivedFile, uploadDTO.fileURI());

        Files file = Files.builder()
                .originalFilename(uploadDTO.originalFilename())
                .savedFilename(uploadDTO.savedFilename())
                .fileType(uploadDTO.fileType())
                .fileURI(uploadDTO.fileURI())
                .build();

        Files savedFile = filesRepository.save(file);
        return new FileResponse(savedFile.getId(), fileUtil.encodedImage(file));
    }

    private void saveFile(MultipartFile receivedFile, String fileURI) throws IOException {
        File targetFile = new File(fileURI);

        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        receivedFile.transferTo(targetFile);
    }

    public FileResponse getEncodedFile(Long fileId) throws IOException {
        Files files = filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_EXIST));

        return new FileResponse(fileId, fileUtil.encodedImage(files));
    }

    public UrlResource getFile(Long fileId) throws MalformedURLException {
        Files files = filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_EXIST));

        return new UrlResource("file:" + files.getFileURI());
    }
}
