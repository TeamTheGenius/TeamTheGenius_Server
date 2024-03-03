package com.genius.gitget.challenge.myChallenge.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.domain.RewardStatus;
import lombok.Builder;

@Builder
public record DoneResponse(
        Long instanceId,
        int pointPerPerson,
        JoinResult joinResult,
        boolean canGetReward,
        int numOfPointItem,
        int rewardPoints,
        double achievementRate
) {
    public static DoneResponse createNotRewarded(Instance instance, Participant participant,
                                                 int numOfPointItem) {
        return DoneResponse.builder()
                .instanceId(instance.getId())
                .pointPerPerson(instance.getPointPerPerson())
                .joinResult(participant.getJoinResult())
                .canGetReward(canGetReward(participant))
                .numOfPointItem(numOfPointItem)
                .build();
    }

    public static DoneResponse createRewarded(Instance instance, Participant participant,
                                              double achievementRate) {
        return DoneResponse.builder()
                .instanceId(instance.getId())
                .pointPerPerson(instance.getPointPerPerson())
                .joinResult(participant.getJoinResult())
                .canGetReward(false)
                .rewardPoints(participant.getRewardPoints())
                .achievementRate(achievementRate)
                .build();
    }

    private static boolean canGetReward(Participant participant) {
        return (participant.getRewardStatus() == RewardStatus.NO) &&
                (participant.getJoinResult() == JoinResult.SUCCESS);
    }
}
