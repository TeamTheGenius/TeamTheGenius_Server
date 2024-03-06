package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.PASSED;

import com.genius.gitget.challenge.certification.service.CertificationProvider;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.Participant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProgressUpdater {
    private final InstanceProvider instanceProvider;
    private final CertificationProvider certificationProvider;
    private final double SUCCESS_THRESHOLD = 85.5;

    @Transactional
    public void updateToActivity(LocalDate currentDate) {
        List<Instance> preActivities = instanceProvider.findAllByProgress(Progress.PREACTIVITY);
        for (Instance preActivity : preActivities) {
            LocalDate startedDate = preActivity.getStartedDate().toLocalDate();
            LocalDate completedDate = preActivity.getCompletedDate().toLocalDate();

            if (currentDate.isAfter(startedDate) && currentDate.isBefore(completedDate)) {
                preActivity.updateProgress(Progress.ACTIVITY);
            }
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

            if (startedDate.isAfter(currentDate) && completedDate.isAfter(currentDate)) {
                updateParticipants(instance, currentDate);
            }
        }
    }

    public void updateParticipants(Instance instance, LocalDate currentDate) {
        instance.updateProgress(Progress.DONE);
        for (Participant participant : instance.getParticipantList()) {
            int totalAttempt = instance.getTotalAttempt();
            int successAttempt = calculateSuccess(participant.getId(), currentDate);

            participant.updateJoinResult(calculateJoinResult(totalAttempt, successAttempt));
        }
    }

    private JoinResult calculateJoinResult(int totalAttempt, int successAttempt) {
        double successPercent = getSuccessPercent(successAttempt, totalAttempt);
        if (successPercent >= SUCCESS_THRESHOLD) {
            return JoinResult.SUCCESS;
        }
        return JoinResult.FAIL;
    }

    private int calculateSuccess(Long participantId, LocalDate currentDate) {
        int certificated = certificationProvider.countByStatus(participantId, CERTIFICATED, currentDate);
        int passed = certificationProvider.countByStatus(participantId, PASSED, currentDate);
        return certificated + passed;
    }

    private double getSuccessPercent(int successCount, int totalCount) {
        double successPercent = (double) successCount / (double) totalCount * 100;
        return Math.round(successPercent * 100 / 100.0);
    }
}
