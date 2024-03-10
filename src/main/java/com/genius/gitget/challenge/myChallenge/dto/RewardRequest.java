package com.genius.gitget.challenge.myChallenge.dto;

import com.genius.gitget.challenge.user.domain.User;
import java.time.LocalDate;

public record RewardRequest(
        User user,
        Long instanceId,
        Boolean canUseItem,
        LocalDate targetDate
) {
}
