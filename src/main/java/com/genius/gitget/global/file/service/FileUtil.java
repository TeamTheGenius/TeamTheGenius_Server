package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_COPIED;
import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_EXIST;
import static com.genius.gitget.global.util.exception.ErrorCode.IMAGE_NOT_ENCODED;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_SUPPORTED_EXTENSION;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.file.dto.UploadDTO;
import com.genius.gitget.global.util.exception.BusinessException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
    private static final List<String> validExtensions = List.of("jpg", "jpeg", "png", "gif");

    public static String encodedImage(Files files) {
        try {
            UrlResource urlResource = new UrlResource("file:" + files.getFileURI());

            byte[] encode = Base64.getEncoder().encode(urlResource.getContentAsByteArray());
            return new String(encode, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BusinessException(IMAGE_NOT_ENCODED);
        }
    }

    public static UploadDTO getUploadInfo(MultipartFile file, String typeStr, final String UPLOAD_PATH) {
        String originalFilename = file.getOriginalFilename();
        String savedFilename = getSavedFilename(originalFilename);
        FileType fileType = FileType.findType(typeStr);

        return UploadDTO.builder()
                .fileType(fileType)
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .fileURI(UPLOAD_PATH + fileType.getPath() + savedFilename)
                .build();
    }

    public static UpdateDTO getUpdateInfo(MultipartFile file, FileType fileType, final String UPLOAD_PATH) {
        String originalFilename = file.getOriginalFilename();
        String savedFilename = getSavedFilename(originalFilename);

        return UpdateDTO.builder()
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .fileURI(UPLOAD_PATH + fileType.getPath() + savedFilename)
                .build();
    }

    public static UploadDTO getCopyInfo(Files files, FileType fileType, final String UPLOAD_PATH) {
        String originalFilename = files.getOriginalFilename();
        String savedFilename = getSavedFilename(originalFilename);

        return UploadDTO.builder()
                .fileType(fileType)
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .fileURI(UPLOAD_PATH + fileType.getPath() + savedFilename)
                .build();
    }

    public static void copyImage(String originFilePath, String copyFilePath) {
        File originFile = new File(originFilePath);
        File copyFile = new File(copyFilePath);

        try {
            java.nio.file.Files.copy(originFile.toPath(), copyFile.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            throw new BusinessException(FILE_NOT_COPIED);
        }
    }

    public static void validateFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || Objects.equals(originalFilename, "")) {
            throw new BusinessException(FILE_NOT_EXIST);
        }

        String extension = extractExtension(originalFilename);
        if (validExtensions.stream()
                .noneMatch(ex -> ex.equals(extension))) {
            throw new BusinessException(NOT_SUPPORTED_EXTENSION);
        }
    }

    public static String getSavedFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = extractExtension(originalFilename);

        return uuid + "." + extension;
    }

    private static String extractExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return filename.substring(index + 1).toLowerCase();
    }
}
