package com.genius.gitget.challenge.myChallenge.dto;

import com.genius.gitget.challenge.user.domain.User;
import java.time.LocalDate;

public record RewardRequest(
        User user,
        Long instanceId,
        LocalDate targetDate
) {

    public static RewardRequest of(User user, Long instanceId, LocalDate targetDate) {
        return new RewardRequest(user, instanceId, targetDate);
    }
}
