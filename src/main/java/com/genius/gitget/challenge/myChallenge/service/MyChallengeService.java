package com.genius.gitget.challenge.myChallenge.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.participant.domain.JoinResult.SUCCESS;
import static com.genius.gitget.challenge.participant.domain.RewardStatus.NO;
import static com.genius.gitget.challenge.participant.domain.RewardStatus.YES;
import static com.genius.gitget.store.item.domain.ItemCategory.CERTIFICATION_PASSER;
import static com.genius.gitget.store.item.domain.ItemCategory.POINT_MULTIPLIER;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.service.CertificationProvider;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantProvider;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.service.ItemService;
import com.genius.gitget.store.item.service.OrdersService;
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
    private final FilesService filesService;
    private final ParticipantProvider participantProvider;
    private final CertificationProvider certificationProvider;
    private final ItemService itemService;
    private final OrdersService ordersService;


    public List<PreActivityResponse> getPreActivityInstances(User user, LocalDate targetDate) {
        List<PreActivityResponse> preActivity = new ArrayList<>();
        List<Participant> participants = participantProvider.findJoinedByProgress(user.getId(), Progress.PREACTIVITY);

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();
            FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());

            PreActivityResponse preActivityResponse = PreActivityResponse.builder()
                    .instanceId(instance.getId())
                    .title(instance.getTitle())
                    .participantCount(instance.getParticipantCount())
                    .pointPerPerson(instance.getPointPerPerson())
                    .remainDays(DateUtil.getRemainDaysToStart(participant.getStartedDate(), targetDate))
                    .fileResponse(fileResponse)
                    .build();
            preActivity.add(preActivityResponse);
        }

        return preActivity;
    }

    public List<DoneResponse> getDoneInstances(User user, LocalDate targetDate) {
        List<DoneResponse> done = new ArrayList<>();
        List<Participant> participants = participantProvider.findDoneInstances(user.getId());

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();
            FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
            double achievementRate = getAchievementRate(instance, participant.getId(), targetDate);

            // 포인트를 아직 수령하지 않았을 때
            if (participant.getRewardStatus() == NO) {
                Item item = itemService.findAllByCategory(POINT_MULTIPLIER).get(0);
                int numOfPassItem = ordersService.countNumOfItem(user, item.getId());
                DoneResponse doneResponse = DoneResponse.createNotRewarded(
                        instance, participant, numOfPassItem, achievementRate, fileResponse);
                doneResponse.setItemId(item.getId());
                done.add(doneResponse);
                continue;
            }

            // 포인트를 수령했을 때
            DoneResponse doneResponse = DoneResponse.createRewarded(
                    instance, participant, achievementRate, fileResponse);
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
            FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
            Certification certification = certificationProvider.findByDate(targetDate, participant.getId())
                    .orElse(getDummyCertification());

            //TODO: 로직 수정 필요
            Item item = itemService.findAllByCategory(CERTIFICATION_PASSER).get(0);
            int numOfPassItem = ordersService.countNumOfItem(user, item.getId());

            ActivatedResponse activatedResponse = ActivatedResponse.create(
                    instance, certification.getCertificationStatus(),
                    numOfPassItem, participant.getRepositoryName(), fileResponse
            );
            activatedResponse.setItemId(item.getId());
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
    public DoneResponse getRewards(RewardRequest rewardRequest, boolean useItem) {
        User user = userService.findUserById(rewardRequest.user().getId());
        Participant participant = participantProvider.findByJoinInfo(user.getId(), rewardRequest.instanceId());
        Instance instance = participant.getInstance();

        FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());

        validRewardCondition(participant);

        int rewardPoints = instance.getPointPerPerson();
        if (useItem) {
            rewardPoints *= 2;
        }

        user.updatePoints((long) rewardPoints);
        double achievementRate = getAchievementRate(instance, participant.getId(), rewardRequest.targetDate());

        participant.getRewards(rewardPoints);
        return DoneResponse.createRewarded(instance, participant, achievementRate, fileResponse);
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