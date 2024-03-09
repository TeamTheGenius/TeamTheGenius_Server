package com.genius.gitget.challenge.likes.dto;

import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;
import lombok.Data;

@Data
public class UserLikesResponse {
    private Long likesId;
    private Long instanceId;
    private String title;
    private int pointPerPerson;
    private FileResponse fileResponse;

    @Builder
    public UserLikesResponse(Long likesId, Long instanceId, String title, int pointPerPerson,
                             FileResponse fileResponse) {
        this.likesId = likesId;
        this.instanceId = instanceId;
        this.title = title;
        this.pointPerPerson = pointPerPerson;
        this.fileResponse = fileResponse;
    }
}
