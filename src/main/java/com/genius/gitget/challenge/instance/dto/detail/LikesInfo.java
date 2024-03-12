package com.genius.gitget.challenge.instance.dto.detail;

import lombok.Builder;

@Builder
public record LikesInfo(
        Long likesId,
        boolean isLiked,
        int likesCount
) {

    public static LikesInfo createExist(Long likesId, int likesCount) {
        return LikesInfo.builder()
                .likesId(likesId)
                .isLiked(true)
                .likesCount(likesCount)
                .build();
    }

    public static LikesInfo createNotExist() {
        return LikesInfo.builder()
                .likesId(0L)
                .isLiked(false)
                .likesCount(0)
                .build();
    }
}
