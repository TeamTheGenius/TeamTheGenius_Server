package com.genius.gitget.schedule.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.PASSED;

import com.genius.gitget.challenge.certification.service.CertificationService;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.service.InstanceProvider;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.Participant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProgressService {
    private final InstanceProvider instanceProvider;
    private final CertificationService certificationService;
    private final double SUCCESS_THRESHOLD = 85;

    @Transactional
    public void updateToActivity(LocalDate currentDate) {
        List<Instance> preActivities = instanceProvider.findAllByProgress(Progress.PREACTIVITY);
        for (Instance preActivity : preActivities) {
            LocalDate startedDate = preActivity.getStartedDate().toLocalDate();
            LocalDate completedDate = preActivity.getCompletedDate().toLocalDate();

            if (isUpdatableToActivity(startedDate, currentDate) && currentDate.isBefore(completedDate)) {
                updateActivityInstance(preActivity);
            }
        }
    }

    private boolean isUpdatableToActivity(LocalDate startedDate, LocalDate currentDate) {
        return currentDate.isEqual(startedDate) || currentDate.isAfter(startedDate);
    }

    private void updateActivityInstance(Instance preActivity) {
        preActivity.updateProgress(Progress.ACTIVITY);
        for (Participant participant : preActivity.getParticipantList()) {
            participant.updateJoinResult(JoinResult.PROCESSING);
        }
    }

    @Transactional
    public void updateToDone(LocalDate currentDate) {
        List<Instance> instances = new ArrayList<>();
        instances.addAll(instanceProvider.findAllByProgress(Progress.PREACTIVITY));
        instances.addAll(instanceProvider.findAllByProgress(Progress.ACTIVITY));

        for (Instance instance : instances) {
            LocalDate startedDate = instance.getStartedDate().toLocalDate();
            LocalDate completedDate = instance.getCompletedDate().toLocalDate();

            if (currentDate.isAfter(startedDate) && currentDate.isAfter(completedDate)) {
                updateDoneInstance(instance, currentDate);
            }
        }
    }

    private void updateDoneInstance(Instance instance, LocalDate currentDate) {
        instance.updateProgress(Progress.DONE);
        for (Participant participant : instance.getParticipantList()) {
            int totalAttempt = instance.getTotalAttempt();
            int successAttempt = getSuccessAttempt(participant.getId(), currentDate);

            JoinResult joinResult = getJoinResult(totalAttempt, successAttempt);
            participant.updateJoinResult(joinResult);
        }
    }

    private JoinResult getJoinResult(int totalAttempt, int successAttempt) {
        double successPercent = getSuccessPercent(successAttempt, totalAttempt);
        if (successPercent >= SUCCESS_THRESHOLD) {
            return JoinResult.SUCCESS;
        }
        return JoinResult.FAIL;
    }

    private int getSuccessAttempt(Long participantId, LocalDate currentDate) {
        int certificated = certificationService.countByStatus(participantId, CERTIFICATED, currentDate);
        int passed = certificationService.countByStatus(participantId, PASSED, currentDate);
        return certificated + passed;
    }

    private double getSuccessPercent(int successCount, int totalCount) {
        double successPercent = (double) successCount / (double) totalCount * 100;
        return Math.round(successPercent * 100 / 100.0);
    }
}
