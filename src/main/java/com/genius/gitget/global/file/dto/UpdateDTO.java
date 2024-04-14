package com.genius.gitget.global.file.dto;

import lombok.Builder;

@Builder
public record UpdateDTO(
        String originalFilename,
        String savedFilename,
        String fileURI
) {

    public static UpdateDTO of(UploadDTO uploadDTO) {
        return UpdateDTO.builder()
                .originalFilename(uploadDTO.originalFilename())
                .savedFilename(uploadDTO.savedFilename())
                .fileURI(uploadDTO.fileURI())
                .build();
    }
}
