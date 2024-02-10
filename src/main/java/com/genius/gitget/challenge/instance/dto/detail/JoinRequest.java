package com.genius.gitget.challenge.instance.dto.detail;

import lombok.Builder;

@Builder
public record JoinRequest(
        Long instanceId,
        String repository
) {
}
