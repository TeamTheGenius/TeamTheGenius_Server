package com.genius.gitget.global.file.dto;

import lombok.Builder;

@Builder
public record UpdateDTO(
        String originalFilename,
        String savedFilename,
        String fileURI
) {

    public static UpdateDTO of(FileDTO fileDTO) {
        return UpdateDTO.builder()
                .originalFilename(fileDTO.originalFilename())
                .savedFilename(fileDTO.savedFilename())
                .fileURI(fileDTO.fileURI())
                .build();
    }
}
