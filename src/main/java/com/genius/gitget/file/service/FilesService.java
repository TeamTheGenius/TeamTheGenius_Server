package com.genius.gitget.file.service;

import com.genius.gitget.file.domain.Files;
import com.genius.gitget.file.dto.UploadDTO;
import com.genius.gitget.file.repository.FilesRepository;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Long uploadFile(MultipartFile receivedFile, String typeStr) throws IOException {
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
        return savedFile.getId();
    }

    private void saveFile(MultipartFile receivedFile, String fileURI) throws IOException {
        File targetFile = new File(fileURI);

        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        receivedFile.transferTo(targetFile);
    }

}
