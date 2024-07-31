package com.genius.gitget.challenge.myChallenge.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.domain.RewardStatus;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.store.item.dto.OrderResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoneResponse extends OrderResponse {
    private Long instanceId;
    private String title;
    private int pointPerPerson;
    private JoinResult joinResult;
    private boolean canGetReward;
    private int numOfPointItem;
    private int rewardedPoints;
    private double achievementRate;
    private FileResponse fileResponse;

    @Builder
    public DoneResponse(Long instanceId, String title, int pointPerPerson, JoinResult joinResult, boolean canGetReward,
                        int numOfPointItem, int rewardedPoints, double achievementRate, FileResponse fileResponse) {

        this.instanceId = instanceId;
        this.title = title;
        this.pointPerPerson = pointPerPerson;
        this.joinResult = joinResult;
        this.canGetReward = canGetReward;
        this.numOfPointItem = numOfPointItem;
        this.rewardedPoints = rewardedPoints;
        this.achievementRate = achievementRate;
        this.fileResponse = fileResponse;
    }

    public static DoneResponse createNotRewarded(Instance instance,
                                                 Participant participant,
                                                 int numOfPointItem, FileResponse fileResponse) {
        return DoneResponse.builder()
                .title(instance.getTitle())
                .instanceId(instance.getId())
                .pointPerPerson(instance.getPointPerPerson())
                .joinResult(participant.getJoinResult())
                .canGetReward(canGetReward(participant))
                .numOfPointItem(numOfPointItem)
                .fileResponse(fileResponse)
                .build();
    }

    public static DoneResponse createRewarded(Instance instance, Participant participant,
                                              double achievementRate, FileResponse fileResponse) {
        return DoneResponse.builder()
                .title(instance.getTitle())
                .instanceId(instance.getId())
                .pointPerPerson(instance.getPointPerPerson())
                .joinResult(participant.getJoinResult())
                .canGetReward(false)
                .rewardedPoints(participant.getRewardPoints())
                .achievementRate(achievementRate)
                .fileResponse(fileResponse)
                .build();
    }

    private static boolean canGetReward(Participant participant) {
        return (participant.getRewardStatus() == RewardStatus.NO) &&
                (participant.getJoinResult() == JoinResult.SUCCESS);
    }
}
