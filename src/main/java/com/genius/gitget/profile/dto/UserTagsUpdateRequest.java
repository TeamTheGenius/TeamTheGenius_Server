package com.genius.gitget.profile.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
public class UserTagsUpdateRequest {
    private List<String> tags;

    @Builder
    public UserTagsUpdateRequest(List<String> tags) {
        this.tags = tags;
    }
}
