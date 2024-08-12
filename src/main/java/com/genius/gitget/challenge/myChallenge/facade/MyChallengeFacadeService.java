package com.genius.gitget.challenge.myChallenge.facade;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static com.genius.gitget.challenge.participant.domain.RewardStatus.NO;
import static com.genius.gitget.store.item.domain.ItemCategory.CERTIFICATION_PASSER;
import static com.genius.gitget.store.item.domain.ItemCategory.POINT_MULTIPLIER;

import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.service.CertificationService;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
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
public class MyChallengeFacadeService implements MyChallengeFacade {
    private final FilesService filesService;
    private final ParticipantService participantService;
    private final CertificationService certificationService;
    private final ItemService itemService;
    private final OrdersService ordersService;


    @Override
    public List<PreActivityResponse> getPreActivityInstances(User user, LocalDate targetDate) {
        List<PreActivityResponse> preActivity = new ArrayList<>();
        List<Participant> participants = participantService.findJoinedByProgress(user.getId(), Progress.PREACTIVITY);

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();
            FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
            int remainDays = DateUtil.getRemainDaysToStart(participant.getStartedDate(), targetDate);

            PreActivityResponse preActivityResponse = PreActivityResponse.of(instance, remainDays, fileResponse);
            preActivity.add(preActivityResponse);
        }

        return preActivity;
    }

    @Override
    public List<ActivatedResponse> getActivatedInstances(User user, LocalDate targetDate) {
        List<ActivatedResponse> activated = new ArrayList<>();
        List<Participant> participants = participantService.findJoinedByProgress(user.getId(), Progress.ACTIVITY);

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();
            FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());

            Certification certification = certificationService.findOrSave(participant, NOT_YET, targetDate);

            Item item = itemService.findAllByCategory(CERTIFICATION_PASSER).get(0);
            int numOfPassItem = ordersService.countNumOfItem(user, item.getId());

            ActivatedResponse activatedResponse = ActivatedResponse.of(
                    instance, certification.getCertificationStatus(),
                    numOfPassItem, participant.getRepositoryName(), fileResponse
            );
            activatedResponse.setItemId(item.getId());
            activated.add(activatedResponse);
        }
        return activated;
    }

    @Override
    public List<DoneResponse> getDoneInstances(User user, LocalDate targetDate) {
        List<DoneResponse> done = new ArrayList<>();
        List<Participant> participants = participantService.findDoneInstances(user.getId());

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();
            FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
            double achievementRate = certificationService.getAchievementRate(instance, participant.getId(),
                    targetDate);

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

    @Override
    @Transactional
    public DoneResponse getRewards(RewardRequest rewardRequest) {
        Participant participant = participantService.findByJoinInfo(
                rewardRequest.userId(), rewardRequest.instanceId()
        );
        Instance instance = participant.getInstance();

        FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());

        int rewardPoints = instance.getPointPerPerson();
        participantService.getRewards(participant, rewardPoints);

        double achievementRate = certificationService.getAchievementRate(instance, participant.getId(),
                rewardRequest.targetDate());

        return DoneResponse.createRewarded(instance, participant, achievementRate, fileResponse);
    }
}