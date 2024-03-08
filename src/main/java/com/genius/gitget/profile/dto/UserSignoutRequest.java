package com.genius.gitget.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserSignoutRequest {
    private String reason;

    @Builder
    public UserSignoutRequest(String reason) {
        this.reason = reason;
    }
}
