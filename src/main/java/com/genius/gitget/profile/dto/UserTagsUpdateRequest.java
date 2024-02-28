package com.genius.gitget.profile.dto;

import lombok.Data;

@Data
public class UserTagsUpdateRequest {
    private String identifier;
    private String tags;
}
