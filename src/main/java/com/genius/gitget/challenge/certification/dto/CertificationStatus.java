package com.genius.gitget.challenge.certification.dto;

import lombok.Builder;

@Builder
public record CertificationStatus(
        double successPercent,
        int totalAttempt,
        int currentAttempt,
        int pointPerPerson,
        int successCount,
        int failureCount,
        int remainCount
) {
}
