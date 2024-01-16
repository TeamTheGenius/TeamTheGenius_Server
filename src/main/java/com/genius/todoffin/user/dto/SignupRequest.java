package com.genius.todoffin.user.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record SignupRequest(
        String identifier,
        String nickname,
        String information,
        List<String> interest
) {
}
