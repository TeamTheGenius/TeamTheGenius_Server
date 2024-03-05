package com.genius.gitget.challenge.certification.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record TotalResponse(
        int totalAttempts,
        List<CertificationResponse> certifications
) {
}
