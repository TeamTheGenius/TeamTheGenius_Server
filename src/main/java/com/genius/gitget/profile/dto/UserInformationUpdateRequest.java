package com.genius.gitget.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserInformationUpdateRequest {
    private String nickname;
    private String information;

    @Builder
    public UserInformationUpdateRequest(String nickname, String information) {
        this.nickname = nickname;
        this.information = information;
    }
}
