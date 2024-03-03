package com.genius.gitget.challenge.myChallenge.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.service.CertificationProvider;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import com.genius.gitget.challenge.item.service.UserItemProvider;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.domain.RewardStatus;
import com.genius.gitget.challenge.participant.service.ParticipantProvider;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
                    .remainDays((int) ChronoUnit.DAYS.between(instance.getStartedDate(), targetDate))
                    .build();
            preActivity.add(preActivityResponse);
        }

        return preActivity;
    }

    public List<DoneResponse> getDoneInstances(User user, LocalDate targetDate) {
        List<DoneResponse> done = new ArrayList<>();
        List<Participant> participants = participantProvider.findJoinedByProgress(user.getId(), Progress.DONE);

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();

            // 포인트를 아직 수령하지 않았을 때
            if (participant.getRewardStatus() == RewardStatus.NO) {
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
        int successCount = certificationProvider.countCertificatedByStatus(participantId, CERTIFICATED,
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
            boolean canUseItem = checkItemCondition(certification.getCertificationStatus(), numOfPassItem);

            ActivatedResponse activatedResponse = ActivatedResponse.builder()
                    .instanceId(instance.getId())
                    .title(instance.getTitle())
                    .pointPerPerson(instance.getPointPerPerson())
                    .repository(participant.getRepositoryName())
                    .certificateStatus(certification.getCertificationStatus())
                    .canUsePassItem(canUseItem)
                    .numOfPassItem(canUseItem ? numOfPassItem : null)
                    .build();
            activated.add(activatedResponse);
        }
        return activated;
    }

    private boolean checkItemCondition(CertificateStatus certificateStatus, int numOfPassItem) {
        return (certificateStatus == CertificateStatus.NOT_YET) && (numOfPassItem > 0);
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
        if (!canGetReward(participant)) {
            throw new BusinessException(ErrorCode.CAN_NOT_GET_REWARDS);
        }

        int pointPerPerson = instance.getPointPerPerson();
        int rewardPoints = pointPerPerson;

        if (rewardRequest.useItem()) {
            UserItem userItem = userItemProvider.findUserItemByUser(user.getId(), ItemCategory.POINT_MULTIPLIER);
            userItem.useItem();
            rewardPoints = pointPerPerson * 2;
        }

        user.updatePoints(rewardPoints);
        double achievementRate = getAchievementRate(instance, participant.getId(), rewardRequest.targetDate());
        return DoneResponse.createRewarded(instance, participant, achievementRate);
    }

    private boolean canGetReward(Participant participant) {
        return (participant.getRewardStatus() == RewardStatus.NO) &&
                (participant.getJoinResult() == JoinResult.SUCCESS);
    }
}