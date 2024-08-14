package com.genius.gitget.global.file.dto;

public record FileResponse(
        Long fileId,
        String encodedFile) {

    public static FileResponse createExistFile(Long filesId, String encodedFile) {
        return new FileResponse(filesId, encodedFile);
    }

    public static FileResponse createNotExistFile() {
        return new FileResponse(0L, "");
    }
}
