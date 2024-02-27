package com.genius.gitget.challenge.myChallenge.dto;

import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import lombok.Builder;

@Builder
public record DoneResponse(
        Long instanceId,
        int pointPerPerson,
        int rewardPoints,
        double achievementRate,
        JoinResult joinResult
) {
}
