package com.genius.gitget.challenge.myChallenge.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.participant.domain.JoinResult.SUCCESS;
import static com.genius.gitget.challenge.participant.domain.RewardStatus.NO;
import static com.genius.gitget.challenge.participant.domain.RewardStatus.YES;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.service.CertificationProvider;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import com.genius.gitget.challenge.item.service.UserItemProvider;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantProvider;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyChallengeService {
    private final UserService userService;
    private final ParticipantProvider participantProvider;
    private final CertificationProvider certificationProvider;
    private final UserItemProvider userItemProvider;


    public List<PreActivityResponse> getPreActivityInstances(User user, LocalDate targetDate) {
        List<PreActivityResponse> preActivity = new ArrayList<>();
        List<Participant> participants = participantProvider.findJoinedByProgress(user.getId(), Progress.PREACTIVITY);

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();

            PreActivityResponse preActivityResponse = PreActivityResponse.builder()
                    .instanceId(instance.getId())
                    .title(instance.getTitle())
                    .participantCount(instance.getParticipantCount())
                    .pointPerPerson(instance.getPointPerPerson())
                    .remainDays(DateUtil.getRemainDaysToStart(participant.getStartedDate(), targetDate))
                    .fileResponse(FileResponse.create(instance.getFiles()))
                    .build();
            preActivity.add(preActivityResponse);
        }

        return preActivity;
    }

    //TODO: 사용자의 달성률이 85%가 되지 않았을 때에는 joinResult가 Fail이어야 함
    public List<DoneResponse> getDoneInstances(User user, LocalDate targetDate) {
        List<DoneResponse> done = new ArrayList<>();
        List<Participant> participants = participantProvider.findJoinedByProgress(user.getId(), Progress.DONE);

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();

            // 포인트를 아직 수령하지 않았을 때
            if (participant.getRewardStatus() == NO) {
                int numOfPassItem = userItemProvider.countNumOfItem(user, ItemCategory.POINT_MULTIPLIER);
                DoneResponse doneResponse = DoneResponse.createNotRewarded(instance, participant, numOfPassItem);
                done.add(doneResponse);
                continue;
            }

            // 포인트를 수령했을 때
            double achievementRate = getAchievementRate(instance, participant.getId(), targetDate);
            DoneResponse doneResponse = DoneResponse.createRewarded(instance, participant, achievementRate);
            done.add(doneResponse);
        }

        return done;
    }

    private double getAchievementRate(Instance instance, Long participantId, LocalDate targetDate) {
        int totalAttempt = instance.getTotalAttempt();
        int successCount = certificationProvider.countByStatus(participantId, CERTIFICATED,
                targetDate);

        double successPercent = (double) successCount / (double) totalAttempt * 100;
        return Math.round(successPercent * 100 / 100.0);
    }

    public List<ActivatedResponse> getActivatedInstances(User user, LocalDate targetDate) {
        List<ActivatedResponse> activated = new ArrayList<>();
        List<Participant> participants = participantProvider.findJoinedByProgress(user.getId(), Progress.ACTIVITY);

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();
            Certification certification = certificationProvider.findByDate(targetDate, participant.getId())
                    .orElse(getDummyCertification());
            int numOfPassItem = userItemProvider.countNumOfItem(user, ItemCategory.CERTIFICATION_PASSER);

            ActivatedResponse activatedResponse = ActivatedResponse.create(
                    instance, certification.getCertificationStatus(),
                    numOfPassItem, participant.getRepositoryName()
            );
            activated.add(activatedResponse);
        }
        return activated;
    }

    private Certification getDummyCertification() {
        return Certification.builder()
                .currentAttempt(0)
                .certificationStatus(CertificateStatus.NOT_YET)
                .certificatedAt(null)
                .certificationLinks(null)
                .build();
    }

    @Transactional
    public DoneResponse getRewards(RewardRequest rewardRequest) {
        User user = userService.findUserById(rewardRequest.user().getId());
        Participant participant = participantProvider.findByJoinInfo(user.getId(), rewardRequest.instanceId());
        Instance instance = participant.getInstance();

        validRewardCondition(participant);

        int pointPerPerson = instance.getPointPerPerson();
        int rewardPoints = pointPerPerson;

        if (rewardRequest.canUseItem()) {
            UserItem userItem = userItemProvider.findUserItemByUser(user.getId(), ItemCategory.POINT_MULTIPLIER);
            userItem.useItem();
            rewardPoints = pointPerPerson * 2;
        }

        user.updatePoints((long) rewardPoints);
        double achievementRate = getAchievementRate(instance, participant.getId(), rewardRequest.targetDate());

        participant.getRewards(rewardPoints);
        return DoneResponse.createRewarded(instance, participant, achievementRate);
    }

    private void validRewardCondition(Participant participant) {
        if (participant.getJoinResult() != SUCCESS) {
            throw new BusinessException(ErrorCode.CAN_NOT_GET_REWARDS);
        }
        if (participant.getRewardStatus() == YES) {
            throw new BusinessException(ErrorCode.ALREADY_REWARDED);
        }
    }
}