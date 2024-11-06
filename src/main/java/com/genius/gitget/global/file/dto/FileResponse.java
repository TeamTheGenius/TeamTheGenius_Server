package com.genius.gitget.global.file.dto;

public record FileResponse(
        Long fileId,
        String accessURI) {

    public static FileResponse createExistFile(Long filesId, String accessURI) {
        return new FileResponse(filesId, accessURI);
    }

    public static FileResponse createNotExistFile() {
        return new FileResponse(0L, "");
    }
}
