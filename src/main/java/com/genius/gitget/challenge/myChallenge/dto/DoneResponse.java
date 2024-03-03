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
        int numOfPassItem,
        int rewardPoints,
        double achievementRate
) {
    public static DoneResponse createNotRewarded(Instance instance, Participant participant,
                                                 int numOfPassItem) {
        return DoneResponse.builder()
                .instanceId(instance.getId())
                .pointPerPerson(instance.getPointPerPerson())
                .joinResult(participant.getJoinResult())
                .canGetReward(canGetReward(participant))
                .numOfPassItem(numOfPassItem)
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

    //TODO: MyChallengeService와 코드가 중복됨
    private static boolean canGetReward(Participant participant) {
        return (participant.getRewardStatus() == RewardStatus.NO) &&
                (participant.getJoinResult() == JoinResult.SUCCESS);
    }
}