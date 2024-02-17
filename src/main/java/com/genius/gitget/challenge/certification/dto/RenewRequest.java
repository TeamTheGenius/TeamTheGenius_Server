package com.genius.gitget.challenge.certification.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RenewRequest(
        Long instanceId,
        LocalDate targetDate
) {
}
