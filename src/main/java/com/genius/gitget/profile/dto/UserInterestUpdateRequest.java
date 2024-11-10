package com.genius.gitget.profile.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInterestUpdateRequest {
    private List<String> tags;

    @Builder
    public UserInterestUpdateRequest(List<String> tags) {
        this.tags = tags;
    }
}
