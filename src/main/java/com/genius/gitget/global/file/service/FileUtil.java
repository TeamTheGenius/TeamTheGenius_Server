package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.util.exception.ErrorCode.INVALID_FILE_NAME;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_SUPPORTED_EXTENSION;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.CopyDTO;
import com.genius.gitget.global.file.dto.FileDTO;
import com.genius.gitget.global.util.exception.BusinessException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {
    private final List<String> validExtensions = List.of("jpg", "jpeg", "png", "gif");


    public FileDTO getFileDTO(MultipartFile file, FileType fileType, final String UPLOAD_PATH) {
        String originalFilename = file.getOriginalFilename();
        String savedFilename = getSavedFilename(originalFilename);

        return FileDTO.builder()
                .fileType(fileType)
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .fileURI(UPLOAD_PATH + fileType.getPath() + savedFilename)
                .build();
    }

    public CopyDTO getCopyInfo(Files files, FileType fileType, final String UPLOAD_PATH) {
        String originalFilename = files.getOriginalFilename();
        String savedFilename = getSavedFilename(originalFilename);

        return CopyDTO.builder()
                .fileType(fileType)
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .fileURI(UPLOAD_PATH + fileType.getPath() + savedFilename)
                .folderURI(UPLOAD_PATH + fileType.getPath())
                .build();
    }

    public void validateFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || Objects.equals(originalFilename, "")) {
            throw new BusinessException(INVALID_FILE_NAME);
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
