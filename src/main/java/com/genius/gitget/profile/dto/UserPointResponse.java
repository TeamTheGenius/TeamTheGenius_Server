package com.genius.gitget.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserPointResponse {
    private String identifier;
    private Long point;

    @Builder
    public UserPointResponse(String identifier, Long point) {
        this.identifier = identifier;
        this.point = point;
    }
}
