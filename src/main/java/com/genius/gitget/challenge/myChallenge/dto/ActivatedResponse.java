package com.genius.gitget.challenge.myChallenge.dto;

import lombok.Builder;

@Builder
public record ActivatedResponse(
        Long instanceId,
        String title,
        int pointPerPerson,
        String repository,
        String certificateStatus,
        int numOfPassItem,
        boolean canUsePassItem
) {
}
