package com.genius.gitget.global.file.dto;

public record FileResponse(
        Long fileId,
        String encodedFile) {

    public FileResponse(Long fileId) {
        this(fileId, null);
    }
}
