package com.genius.gitget.global.file.dto;

import lombok.Builder;

@Builder
public record UpdateDTO(
        String originalFilename,
        String savedFilename,
        String fileURI
) {
}
