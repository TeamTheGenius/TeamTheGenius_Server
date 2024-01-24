package com.genius.gitget.file.dto;

import com.genius.gitget.file.domain.FileType;
import lombok.Builder;

@Builder
public record UploadDTO(FileType fileType,
                        String originalFilename,
                        String savedFilename,
                        String fileURI) {
}
