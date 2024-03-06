package com.genius.gitget.profile.dto;

import java.util.List;
import lombok.Data;

@Data
public class UserTagsUpdateRequest {
    private List<String> tags;

}
