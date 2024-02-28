package com.genius.gitget.profile.dto;

import lombok.Data;

@Data
public class UserInformationUpdateRequest {
    private String identifier;
    private String nickname;
    private String information;
}
