package com.genius.gitget.global.file.dto;

import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.service.FileUtil;
import java.util.Optional;

public record FileResponse(
        Long fileId,
        String encodedFile) {

    public static FileResponse create(Optional<Files> optionalFiles) {
        if (optionalFiles.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(optionalFiles.get());
    }

    public static FileResponse createExistFile(Files files) {
        return new FileResponse(files.getId(), FileUtil.encodedImage(files));
    }

    public static FileResponse createNotExistFile() {
        return new FileResponse(0L, "none");
    }
}
