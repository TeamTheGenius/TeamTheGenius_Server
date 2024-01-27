package com.genius.gitget.global.file.dto;

import org.springframework.web.multipart.MultipartFile;

public record FileRequest(
        MultipartFile file,
        String type
) {
}
