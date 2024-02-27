package com.genius.gitget.challenge.myChallenge.dto;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import lombok.Builder;

@Builder
public record ActivatedResponse(
        Long instanceId,
        String title,
        int pointPerPerson,
        String repository,
        CertificateStatus certificateStatus,
        boolean canUsePassItem
) {
}
