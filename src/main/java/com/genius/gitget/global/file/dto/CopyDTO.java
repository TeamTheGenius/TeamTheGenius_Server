package com.genius.gitget.global.file.dto;

import com.genius.gitget.global.file.domain.FileType;
import lombok.Builder;

@Builder
public record CopyDTO(FileType fileType,
                      String originalFilename,
                      String savedFilename,
                      String fileURI,
                      String folderURI) {
}
