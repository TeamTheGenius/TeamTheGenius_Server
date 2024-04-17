package com.genius.gitget.profile.dto;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;
import lombok.Data;

@Data
public class UserInformationResponse {
    private String identifier;
    private String nickname;
    private Long frameId;
    private FileResponse fileResponse;

    @Builder
    public UserInformationResponse(String identifier, String nickname, Long frameId, FileResponse fileResponse) {
        this.identifier = identifier;
        this.nickname = nickname;
        this.frameId = frameId;
        this.fileResponse = fileResponse;
    }

    public static UserInformationResponse createByEntity(User findUser, Long frameId, FileResponse fileResponse) {
        return UserInformationResponse.builder()
                .identifier(findUser.getIdentifier())
                .nickname(findUser.getNickname())
                .frameId(frameId)
                .fileResponse(fileResponse)
                .build();
    }
}
