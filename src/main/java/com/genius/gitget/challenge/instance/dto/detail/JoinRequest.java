package com.genius.gitget.challenge.instance.dto.detail;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record JoinRequest(
        Long instanceId,
        String repository,
        LocalDate todayDate
) {
}
