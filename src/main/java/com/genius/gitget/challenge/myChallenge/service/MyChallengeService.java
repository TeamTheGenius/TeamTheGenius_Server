package com.genius.gitget.challenge.myChallenge.service;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.service.CertificationProvider;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.service.InstanceProvider;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
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
    private final InstanceProvider instanceProvider;
    private final ParticipantProvider participantProvider;
    private final CertificationProvider certificationProvider;


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

    public List<ActivatedResponse> getActivatedInstances(User user, LocalDate targetDate) {
        List<ActivatedResponse> activated = new ArrayList<>();
        List<Participant> participants = participantProvider.findJoinedByProgress(user.getId(), Progress.ACTIVITY);

        for (Participant participant : participants) {
            Instance instance = participant.getInstance();
            Certification certification = certificationProvider.findByDate(targetDate, participant.getId())
                    .orElse(getDummy());

            ActivatedResponse activatedResponse = ActivatedResponse.builder()
                    .instanceId(instance.getId())
                    .title(instance.getTitle())
                    .pointPerPerson(instance.getPointPerPerson())
                    .repository(participant.getRepositoryName())
                    .certificateStatus(certification.getCertificationStatus())
                    .canUsePassItem(checkItemCondition(certification.getCertificationStatus()))
                    .build();
            activated.add(activatedResponse);
        }
        return activated;
    }

    private boolean checkItemCondition(CertificateStatus certificateStatus) {
        //TODO:사용자가 패스 아이템을 가지고 있는지 여부 추가 확인 필요
        return certificateStatus == CertificateStatus.NOT_YET;
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





