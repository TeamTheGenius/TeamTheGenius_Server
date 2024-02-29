package com.genius.gitget.challenge.certification.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record WeekResponse(
        Long userId,
        String nickname,
        List<RenewResponse> renewResponses
) {
}
