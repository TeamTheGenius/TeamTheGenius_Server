package com.genius.gitget.challenge.certification.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CertificationRequest(
        Long instanceId,
        LocalDate targetDate
) {
}
