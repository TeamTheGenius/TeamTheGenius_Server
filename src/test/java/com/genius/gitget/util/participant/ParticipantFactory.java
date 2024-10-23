package com.genius.gitget.util.participant;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.domain.RewardStatus;
import com.genius.gitget.challenge.user.domain.User;

public class ParticipantFactory {
    /**
     * 시작 전인 참여 정보 엔티티 만들어서 반환
     * user, instance를 받아서 연관관계 설정 후 반환
     */
    public static Participant createPreActivity(User user, Instance instance) {
        Participant participant = Participant.builder()
                .joinResult(JoinResult.READY)
                .joinStatus(JoinStatus.YES)
                .build();
        participant.setUserAndInstance(user, instance);
        participant.updateRepository("targetRepo");

        return participant;
    }

    /**
     * 진행 중인 참여 정보 엔티티 만들어서 반환
     * user, instance를 받아서 연관관계 설정 후 반환
     */
    public static Participant createProcessing(User user, Instance instance) {
        Participant participant = Participant.builder()
                .joinResult(JoinResult.PROCESSING)
                .joinStatus(JoinStatus.YES)
                .build();
        participant.setUserAndInstance(user, instance);
        participant.updateRepository("targetRepo");

        return participant;
    }

    /**
     * 참여 정보에 대해 JoinResult(참여 결과 - 시작전, 진행중, 실패, 성공) 설정 후 반환
     * user, instance를 받아서 연관관계 설정 후 반환
     */
    public static Participant createByJoinResult(User user, Instance instance, JoinResult joinResult) {
        Participant participant = Participant.builder()
                .joinResult(joinResult)
                .joinStatus(JoinStatus.YES)
                .build();
        participant.setUserAndInstance(user, instance);
        participant.updateRepository("targetRepo");

        return participant;
    }

    /**
     * 챌린지가 끝난 참여 정보에 대해, RewardStatus(보상 수령 상태)에 대한 값을 설정 후 반환
     * user, instance를 받아서 연관관계 설정 후 반환
     */
    public static Participant createByRewardStatus(User user, Instance instance, JoinResult joinResult,
                                                   RewardStatus rewardStatus) {
        Participant participant = Participant.builder()
                .joinResult(joinResult)
                .joinStatus(JoinStatus.YES)
                .rewardStatus(rewardStatus)
                .build();
        participant.setUserAndInstance(user, instance);
        participant.updateRepository("targetRepo");

        return participant;
    }

    public static Participant createQuit(User user, Instance instance, JoinResult joinResult) {
        Participant participant = Participant.builder()
                .joinResult(joinResult)
                .joinStatus(JoinStatus.NO)
                .rewardStatus(RewardStatus.NO)
                .build();
        participant.setUserAndInstance(user, instance);
        participant.updateRepository("targetRepo");

        return participant;
    }
}
