package com.genius.gitget.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserChallengeResultResponse {
    private Integer fail;
    private Integer success;
    private Integer processing;
    private Integer beforeStart;

    @Builder
    public UserChallengeResultResponse(Integer fail, Integer success, Integer processing, Integer beforeStart) {
        this.fail = fail;
        this.success = success;
        this.processing = processing;
        this.beforeStart = beforeStart;
    }
}
