package com.genius.gitget.global.file.service;

import org.springframework.beans.factory.annotation.Value;

public class LocalFileManager implements FileManager {
    private final String UPLOAD_PATH;

    public LocalFileManager(@Value("${file.upload.path}") String UPLOAD_PATH) {
        this.UPLOAD_PATH = UPLOAD_PATH;
    }
}
