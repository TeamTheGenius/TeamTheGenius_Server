package com.genius.gitget.challenge.instance.dto.search;

import com.genius.gitget.challenge.instance.domain.Progress;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

@Getter
@Data
public class InstanceSearchRequest {
    private String keyword;
    private String progress;

    @Builder
    public InstanceSearchRequest(String keyword, String progress) {
        this.keyword = keyword;
        this.progress = progress;
    }
}