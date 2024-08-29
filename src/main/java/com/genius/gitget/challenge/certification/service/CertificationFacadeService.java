package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.PASSED;

import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationInformation;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.InstancePreviewResponse;
import com.genius.gitget.challenge.certification.dto.TotalResponse;
import com.genius.gitget.challenge.certification.dto.WeekResponse;
import com.genius.gitget.challenge.certification.facade.CertificationFacade;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.dto.UserProfileInfo;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationFacadeService implements CertificationFacade {
    private final FilesManager filesManager;

    private final UserService userService;
    private final InstanceService instanceService;
    private final ParticipantService participantService;
    private final GithubService githubService;
    private final CertificationService certificationService;

    @Override
    public WeekResponse getMyWeekCertifications(Long participantId, LocalDate currentDate) {
        Participant participant = participantService.findById(participantId);
        return getWeekResponse(participant, currentDate);
    }

    @Override
    public Slice<WeekResponse> getOthersWeekCertifications(Long userId, Long instanceId,
                                                           LocalDate currentDate, Pageable pageable) {
        Slice<Participant> participants = participantService.findAllByInstanceId(userId, instanceId, pageable);
        return participants.map(
                participant -> getWeekResponse(participant, currentDate)
        );
    }

    private WeekResponse getWeekResponse(Participant participant, LocalDate currentDate) {
        Instance instance = participant.getInstance();
        LocalDate instanceStartDate = instance.getStartedDate().toLocalDate();
        LocalDate weekStartDate = DateUtil.getWeekStartDate(instanceStartDate, currentDate);

        UserProfileInfo userProfileInfo = userService.getUserProfileInfo(participant.getUser());

        if (!instance.isActivatedInstance()) {
            return WeekResponse.create(userProfileInfo, new ArrayList<>());
        }

        List<Certification> certifications = certificationService.findByDuration(
                weekStartDate, currentDate, participant.getId());

        List<CertificationResponse> weekCertifications = getWeekCertifications(
                certifications, instanceStartDate, currentDate);

        return WeekResponse.create(userProfileInfo, weekCertifications);
    }

    private List<CertificationResponse> getWeekCertifications(List<Certification> certifications,
                                                              LocalDate startDate, LocalDate currentDate) {
        List<CertificationResponse> results = new ArrayList<>();
        Map<LocalDate, Certification> weekMap = new HashMap<>();
        for (Certification certification : certifications) {
            weekMap.put(certification.getCertificatedAt(), certification);
        }

        LocalDate weekStartDate = DateUtil.getWeekStartDate(startDate, currentDate).minusDays(1);
        while (weekStartDate.isBefore(currentDate)) {
            weekStartDate = weekStartDate.plusDays(1);
            if (weekMap.containsKey(weekStartDate)) {
                results.add(CertificationResponse.createExist(weekMap.get(weekStartDate)));
                continue;
            }
            results.add(CertificationResponse.createNonExist(0, weekStartDate));
        }
        return results;
    }

    @Override
    public TotalResponse getTotalCertification(Long participantId, LocalDate currentDate) {
        Instance instance = participantService.getInstanceById(participantId);
        LocalDate startDate = instance.getStartedDate().toLocalDate();

        int totalAttempts = instance.getTotalAttempt();
        int curAttempt = DateUtil.getAttemptCount(startDate, currentDate);

        if (instance.getProgress() == Progress.DONE) {
            currentDate = instance.getCompletedDate().toLocalDate();
            curAttempt = totalAttempts;
        }

        List<Certification> certifications = certificationService.findByDuration(
                startDate, currentDate, participantId);

        List<CertificationResponse> totalCertifications = getTotalCertifications(
                certifications, curAttempt, startDate);

        return TotalResponse.builder()
                .totalAttempts(totalAttempts)
                .certifications(totalCertifications)
                .build();
    }

    private List<CertificationResponse> getTotalCertifications(List<Certification> certifications,
                                                               int curAttempt, LocalDate startedDate) {
        List<CertificationResponse> result = new ArrayList<>();
        Map<Integer, Certification> totalMap = new HashMap<>();

        for (Certification certification : certifications) {
            totalMap.put(certification.getCurrentAttempt(), certification);
        }

        startedDate = startedDate.minusDays(1);

        for (int cur = 1; cur <= curAttempt; cur++) {
            startedDate = startedDate.plusDays(1);
            if (totalMap.containsKey(cur)) {
                result.add(CertificationResponse.createExist(totalMap.get(cur)));
                continue;
            }
            result.add(CertificationResponse.createNonExist(cur, startedDate));
        }

        return result;
    }

    @Override
    @Transactional
    public ActivatedResponse passCertification(Long userId, CertificationRequest certificationRequest) {
        Instance instance = instanceService.findInstanceById(certificationRequest.instanceId());
        Participant participant = participantService.findByJoinInfo(userId, instance.getId());
        LocalDate targetDate = certificationRequest.targetDate();

        Certification certification = certificationService.findOrSave(participant, NOT_YET, targetDate);

        instance.validateCertificateCondition(targetDate);
        certification.validatePassCondition();

        certification.updateToPass(targetDate);

        FileResponse fileResponse = filesManager.convertToFileResponse(instance.getFiles());
        return ActivatedResponse.of(instance, certification.getCertificationStatus(),
                0, participant.getRepositoryName(), fileResponse);
    }

    @Override
    @Transactional
    public CertificationResponse updateCertification(User user, CertificationRequest certificationRequest) {
        CompletableFuture<GitHub> githubConnection = githubService.getGithubConnection(user);
        GitHub github = githubConnection.join();
        Instance instance = instanceService.findInstanceById(certificationRequest.instanceId());
        Participant participant = participantService.findByJoinInfo(user.getId(), instance.getId());

        String repositoryName = participant.getRepositoryName();
        LocalDate targetDate = certificationRequest.targetDate();

        instance.validateCertificateCondition(targetDate);

        List<GHPullRequest> pullRequestByDate = githubService.getPullRequestByDate(github, repositoryName, targetDate);
        List<String> filteredPullRequests = githubService.filterValidPR(
            pullRequestByDate, instance.getPrTemplate(targetDate)
        );

        certificationService.findOrSave(participant, NOT_YET, targetDate);

        Certification certification = certificationService.findByDate(targetDate, participant.getId())
                .map(updated -> {
                    updated.validateCertificateCondition();
                    return certificationService.update(updated, targetDate, filteredPullRequests);
                })
                .orElseGet(
                        () -> certificationService.createCertificated(participant, targetDate, filteredPullRequests)
                );

        return CertificationResponse.createExist(certification);
    }

    @Override
    public InstancePreviewResponse getInstancePreview(Long instanceId) {
        Instance instance = instanceService.findInstanceById(instanceId);
        FileResponse fileResponse = filesManager.convertToFileResponse(instance.getFiles());
        return InstancePreviewResponse.createByEntity(instance, fileResponse);
    }

    @Override
    @Transactional
    public CertificationInformation getCertificationInformation(Instance instance, Participant participant,
                                                                LocalDate currentDate) {
        int successCount = 0;
        int failureCount = 0;
        int remainCount = 0;

        int totalAttempt = instance.getTotalAttempt();
        int currentAttempt = 0;

        switch (instance.getProgress()) {
            case PREACTIVITY -> {
                remainCount = instance.getTotalAttempt();
            }
            case ACTIVITY -> {
                currentAttempt = DateUtil.getAttemptCount(instance.getStartedDate().toLocalDate(), currentDate);
                successCount = calculateSuccess(participant.getId(), currentDate);
                failureCount = currentAttempt - successCount;
                remainCount = totalAttempt - currentAttempt;

            }
            case DONE -> {
                currentAttempt = totalAttempt;
                successCount = calculateSuccess(participant.getId(), instance.getCompletedDate().toLocalDate());
                failureCount = totalAttempt - successCount;
            }
        }

        return CertificationInformation.builder()
                .prTemplate(instance.getPrTemplate(currentDate))
                .repository(participant.getRepositoryName())
                .successPercent(getSuccessPercent(successCount, currentAttempt))
                .totalAttempt(totalAttempt)
                .currentAttempt(currentAttempt)
                .pointPerPerson(instance.getPointPerPerson())
                .successCount(successCount)
                .failureCount(failureCount)
                .remainCount(remainCount)
                .build();
    }

    private int calculateSuccess(Long participantId, LocalDate currentDate) {
        int certificated = certificationService.countByStatus(participantId, CERTIFICATED, currentDate);
        int passed = certificationService.countByStatus(participantId, PASSED, currentDate);
        return certificated + passed;
    }

    private double getSuccessPercent(int successCount, int currentAttempt) {
        double successPercent = (double) successCount / (double) currentAttempt * 100;
        return Math.round(successPercent * 100 / 100.0);
    }
}