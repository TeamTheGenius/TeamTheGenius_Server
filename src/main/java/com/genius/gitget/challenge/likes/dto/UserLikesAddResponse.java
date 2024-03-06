package com.genius.gitget.challenge.likes.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserLikesAddResponse {
    private Long likesId;

    @Builder
    public UserLikesAddResponse(Long likesId) {
        this.likesId = likesId;
    }
}
