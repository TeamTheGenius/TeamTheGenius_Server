package com.genius.gitget.global.file.dto;

public record FileResponse(
        Long fileId,
        String source,
        String environment) {

    public static FileResponse createExistFile(Long filesId, String accessURI) {
        return new FileResponse(filesId, accessURI, FileEnv.getFileEnvironment());
    }

    public static FileResponse createNotExistFile() {
        return new FileResponse(0L, "", FileEnv.getFileEnvironment());
    }
}
