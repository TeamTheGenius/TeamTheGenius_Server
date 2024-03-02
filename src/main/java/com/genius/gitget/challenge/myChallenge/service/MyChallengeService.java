package com.genius.gitget.challenge.myChallenge.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.service.CertificationProvider;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.service.UserItemProvider;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.participantinfo.domain.Participant;
import com.genius.gitget.challenge.participantinfo.service.ParticipantProvider;
import com.genius.gitget.challenge.user.domain.User;
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

            DoneResponse doneResponse = DoneResponse.builder()
                    .instanceId(instance.getId())
                    .pointPerPerson(instance.getPointPerPerson())
                    .rewardPoints(instance.getPointPerPerson()) //TODO: 아이템 사용에 따른 적용 필요
                    .joinResult(participant.getJoinResult())
                    .achievementRate(getAchievementRate(instance, participant.getId(), targetDate))
                    .build();
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
                    .orElse(getDummy());
            int numOfPassItem = userItemProvider.findUserItemByUser(user, ItemCategory.CERTIFICATION_PASSER).getCount();
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

    private Certification getDummy() {
        return Certification.builder()
                .currentAttempt(0)
                .certificationStatus(CertificateStatus.NOT_YET)
                .certificatedAt(null)
                .certificationLinks(null)
                .build();
    }
}





