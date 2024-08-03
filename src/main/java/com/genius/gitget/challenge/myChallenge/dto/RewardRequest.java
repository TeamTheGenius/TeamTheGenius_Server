package com.genius.gitget.challenge.myChallenge.dto;

import java.time.LocalDate;

public record RewardRequest(
        Long userId,
        Long instanceId,
        LocalDate targetDate
) {

    public static RewardRequest of(Long userId, Long instanceId, LocalDate targetDate) {
        return new RewardRequest(userId, instanceId, targetDate);
    }
}
