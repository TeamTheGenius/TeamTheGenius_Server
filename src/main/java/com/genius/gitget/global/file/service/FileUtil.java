package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.util.exception.ErrorCode.IMAGE_NOT_EXIST;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_SUPPORTED_EXTENSION;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.dto.UploadDTO;
import com.genius.gitget.global.util.exception.BusinessException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {
    private final List<String> validExtensions = List.of("jpg", "jpeg", "png", "gif");
    private final String uploadPath;

    public FileUtil(@Value("${file.upload.path}") String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public UploadDTO getUploadInfo(MultipartFile file, String typeStr) {
        String originalFilename = file.getOriginalFilename();
        String savedFilename = getSavedFilename(originalFilename);
        FileType fileType = FileType.fineType(typeStr);

        return UploadDTO.builder()
                .fileType(fileType)
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .fileURI(uploadPath + fileType.getPath() + savedFilename)
                .build();
    }

    public void validateFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || Objects.equals(originalFilename, "")) {
            throw new BusinessException(IMAGE_NOT_EXIST);
        }

        String extension = extractExtension(originalFilename);
        if (validExtensions.stream()
                .noneMatch(ex -> ex.equals(extension))) {
            throw new BusinessException(NOT_SUPPORTED_EXTENSION);
        }
    }


    public String getSavedFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = extractExtension(originalFilename);

        return uuid + "." + extension;
    }

    private String extractExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return filename.substring(index + 1).toLowerCase();
    }
}
