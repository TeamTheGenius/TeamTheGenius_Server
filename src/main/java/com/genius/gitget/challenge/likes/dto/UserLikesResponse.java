package com.genius.gitget.challenge.likes.dto;

import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Data
public class UserLikesResponse {
    private Long instanceId;
    private String title;
    private int pointPerPerson;
    private FileResponse fileResponse;

    @Builder
    public UserLikesResponse(Long instanceId, String title, int pointPerPerson, FileResponse fileResponse) {
        this.instanceId = instanceId;
        this.title = title;
        this.pointPerPerson = pointPerPerson;
        this.fileResponse = fileResponse;
    }

    public static UserLikesResponse createByEntity(LikesDTO likesDTO) throws IOException {
        return UserLikesResponse.builder()
                .instanceId(likesDTO.getInstanceId())
                .title(likesDTO.getTitle())
                .pointPerPerson(likesDTO.getPointPerPerson())
                .fileResponse(convertToFileResponse(Optional.ofNullable(likesDTO.getFiles())))
                .build();
    }

    private static FileResponse convertToFileResponse(Optional<Files> files) {
        if (files.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(files.get());
    }

}
