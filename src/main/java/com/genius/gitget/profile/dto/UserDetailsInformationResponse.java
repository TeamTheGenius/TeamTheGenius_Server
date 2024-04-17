package com.genius.gitget.profile.dto;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;
import lombok.Data;

@Data
public class UserDetailsInformationResponse {
    private String identifier;
    private String nickname;
    private String information;
    private Long point;
    private int progressBar;
    private FileResponse fileResponse;

    @Builder
    public UserDetailsInformationResponse(String identifier, String nickname, String information,
                                          Long point, int progressBar, FileResponse fileResponse) {
        this.identifier = identifier;
        this.nickname = nickname;
        this.information = information;
        this.point = point;
        this.fileResponse = fileResponse;
        this.progressBar = progressBar;
    }

    public static UserDetailsInformationResponse createByEntity(User findUser, int participantCount,
                                                                FileResponse fileResponse) {
        return UserDetailsInformationResponse.builder()
                .identifier(findUser.getIdentifier())
                .nickname(findUser.getNickname())
                .information(findUser.getInformation())
                .point(findUser.getPoint())
                .progressBar(participantCount)
                .fileResponse(fileResponse)
                .build();
    }
}
