package com.genius.gitget.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserTagsUpdateRequest {
    private String tags;

    @Builder
    public UserTagsUpdateRequest(String tags) {
        this.tags = tags;
    }
}
