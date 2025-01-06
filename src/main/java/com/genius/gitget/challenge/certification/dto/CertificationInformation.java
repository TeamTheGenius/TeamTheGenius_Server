package com.genius.gitget.challenge.certification.dto;

import lombok.Builder;

@Builder
public record CertificationInformation(
        String prTemplate,
        String repository,
        double successPercent,
        int totalAttempt,
        int currentAttempt,
        int pointPerPerson,
        int successCount,
        int failureCount,
        int remainCount
) {
}
