package com.genius.gitget.profile.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSignoutRequest {
    private String reason;

    @Builder
    public UserSignoutRequest(String reason) {
        this.reason = reason;
    }
}
