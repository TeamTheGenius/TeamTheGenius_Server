package com.genius.gitget.challenge.user.dto;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;

public record UserProfileInfo(
        Long userId,
        String nickname,
        Long frameId,
        FileResponse fileResponse
) {
    public static UserProfileInfo createByEntity(User user, Long frameId) {
        return new UserProfileInfo(user.getId(), user.getNickname(), frameId, FileResponse.create(user.getFiles()));
    }
}
