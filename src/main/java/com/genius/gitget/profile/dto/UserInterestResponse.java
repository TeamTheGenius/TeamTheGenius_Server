package com.genius.gitget.profile.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
public class UserInterestResponse {
    private List<String> tags;

    @Builder
    public UserInterestResponse(List<String> tags) {
        this.tags = tags;
    }
}
