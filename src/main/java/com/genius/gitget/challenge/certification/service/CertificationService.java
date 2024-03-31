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
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.service.InstanceProvider;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantProvider;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.service.OrdersProvider;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationService {
    private final FilesService filesService;
    private final GithubProvider githubProvider;
    private final CertificationProvider certificationProvider;
    private final ParticipantProvider participantProvider;
    private final InstanceProvider instanceProvider;
    private final OrdersProvider ordersProvider;


    public List<CertificationResponse> getWeekCertification(Long participantId, LocalDate currentDate) {
        Instance instance = participantProvider.getInstanceById(participantId);
        if (!instance.isActivatedInstance()) {
            return new ArrayList<>();
        }

        LocalDate startDate = instance.getStartedDate().toLocalDate();
        int curAttempt = DateUtil.getWeekAttempt(startDate, currentDate);
        LocalDate weekStartDate = DateUtil.getWeekStartDate(startDate, currentDate);

        List<Certification> certifications = certificationProvider.findByDuration(
                weekStartDate, currentDate, participantId);
        Map<Integer, Certification> certificationMap = convertToWeekMap(certifications);

        return convertToCertificationResponse(certificationMap, curAttempt, weekStartDate);
    }

    public Slice<WeekResponse> getAllWeekCertification(Long userId, Long instanceId,
                                                       LocalDate currentDate, Pageable pageable) {
        Slice<Participant> participants = participantProvider.findAllByInstanceId(userId, instanceId, pageable);
        return participants.map(
                participant -> convertToWeekResponse(participant, currentDate)
        );
    }

    private WeekResponse convertToWeekResponse(Participant participant, LocalDate currentDate) {
        User user = participant.getUser();
        Instance instance = participant.getInstance();

        LocalDate startDate = instance.getStartedDate().toLocalDate();
        LocalDate weekStartDate = DateUtil.getWeekStartDate(startDate, currentDate);

        FileResponse fileResponse = FileResponse.create(user.getFiles());
        Long frameId = ordersProvider.getUsingFrameItem(user.getId()).getId();

        if (!instance.isActivatedInstance()) {
            return WeekResponse.create(user, frameId, fileResponse, new ArrayList<>());
        }

        List<Certification> certifications = certificationProvider.findByDuration(
                weekStartDate, currentDate, participant.getId());
        Map<Integer, Certification> certificationMap = convertToWeekMap(certifications);

        List<CertificationResponse> certificationResponses = convertToCertificationResponse(
                certificationMap,
                DateUtil.getWeekAttempt(startDate, currentDate),
                weekStartDate);

        return WeekResponse.create(user, frameId, fileResponse, certificationResponses);
    }

    public TotalResponse getTotalCertification(Long participantId, LocalDate currentDate) {
        Instance instance = participantProvider.getInstanceById(participantId);
        LocalDate startDate = instance.getStartedDate().toLocalDate();

        int totalAttempts = instance.getTotalAttempt();
        int curAttempt = DateUtil.getAttemptCount(startDate, currentDate);

        if (instance.getProgress() == Progress.DONE) {
            currentDate = instance.getCompletedDate().toLocalDate();
            curAttempt = totalAttempts;
        }

        List<Certification> certifications = certificationProvider.findByDuration(
                startDate, currentDate, participantId);
        Map<Integer, Certification> certificationMap = convertToTotalMap(certifications);

        List<CertificationResponse> certificationResponses = convertToCertificationResponse(
                certificationMap, curAttempt, startDate);

        return TotalResponse.builder()
                .totalAttempts(totalAttempts)
                .certifications(certificationResponses)
                .build();
    }

    private List<CertificationResponse> convertToCertificationResponse(Map<Integer, Certification> certificationMap,
                                                                       int curAttempt, LocalDate startedDate) {
        List<CertificationResponse> result = new ArrayList<>();

        startedDate = startedDate.minusDays(1);

        for (int cur = 1; cur <= curAttempt; cur++) {
            startedDate = startedDate.plusDays(1);
            if (certificationMap.containsKey(cur)) {
                result.add(CertificationResponse.createExist(certificationMap.get(cur)));
                continue;
            }
            result.add(CertificationResponse.createNonExist(cur, startedDate));
        }

        return result;
    }

    private Map<Integer, Certification> convertToTotalMap(List<Certification> certifications) {
        Map<Integer, Certification> certificationMap = new HashMap<>();

        for (Certification certification : certifications) {
            certificationMap.put(certification.getCurrentAttempt(), certification);
        }
        return certificationMap;
    }

    private Map<Integer, Certification> convertToWeekMap(List<Certification> certifications) {
        Map<Integer, Certification> certificationMap = new HashMap<>();

        for (Certification certification : certifications) {
            DayOfWeek dayOfWeek = certification.getCertificatedAt().getDayOfWeek();
            certificationMap.put(dayOfWeek.ordinal() + 1, certification);
        }
        return certificationMap;
    }

    @Transactional
    public ActivatedResponse passCertification(Long userId, CertificationRequest certificationRequest) {
        Instance instance = instanceProvider.findById(certificationRequest.instanceId());
        Participant participant = participantProvider.findByJoinInfo(userId, instance.getId());
        LocalDate targetDate = certificationRequest.targetDate();

        Optional<Certification> optionalCertification = certificationProvider.findByDate(targetDate,
                participant.getId());

        validCertificationCondition(instance, targetDate);
        validatePassCondition(optionalCertification);

        Certification certification = optionalCertification
                .map(passed -> {
                    passed.updateToPass(targetDate);
                    return passed;
                })
                .orElseGet(() -> {
                    Certification passed = Certification.createPassed(targetDate);
                    passed.setParticipant(participant);
                    certificationProvider.save(passed);
                    return passed;
                });

        return ActivatedResponse.create(instance, certification.getCertificationStatus(), 0,
                participant.getRepositoryName());
    }

    private void validatePassCondition(Optional<Certification> optional) {
        if (optional.isPresent() && optional.get().getCertificationStatus() != NOT_YET) {
            throw new BusinessException(ErrorCode.CAN_NOT_USE_PASS_ITEM);
        }
    }

    @Transactional
    public CertificationResponse updateCertification(User user, CertificationRequest certificationRequest) {
        GitHub gitHub = githubProvider.getGithubConnection(user);
        Instance instance = instanceProvider.findById(certificationRequest.instanceId());
        Participant participant = participantProvider.findByJoinInfo(user.getId(), instance.getId());

        String repositoryName = participant.getRepositoryName();
        LocalDate targetDate = certificationRequest.targetDate();

        validCertificationCondition(instance, targetDate);

        List<String> filteredPullRequests = filterValidPR(
                githubProvider.getPullRequestByDate(gitHub, repositoryName, targetDate),
                instance.getPrTemplate(targetDate)
        );

        Certification certification = certificationProvider.findByDate(targetDate, participant.getId())
                .map(updated -> {
                    if (updated.getCertificationStatus() == PASSED) {
                        throw new BusinessException(ErrorCode.ALREADY_PASSED_CERTIFICATION);
                    }
                    return certificationProvider.update(updated, targetDate, filteredPullRequests);
                })
                .orElseGet(
                        () -> certificationProvider.createCertification(participant, targetDate, filteredPullRequests)
                );

        return CertificationResponse.createExist(certification);
    }

    private List<String> filterValidPR(List<GHPullRequest> ghPullRequests, String prTemplate) {
        return ghPullRequests.stream()
                .filter(ghPullRequest -> {
                    if (ghPullRequest.getBody() == null) {
                        return false;
                    }
                    return ghPullRequest.getBody().contains(prTemplate);
                })
                .map(ghPullRequest -> ghPullRequest.getHtmlUrl().toString())
                .toList();
    }

    private void validCertificationCondition(Instance instance, LocalDate targetDate) {
        if (instance.getProgress() != Progress.ACTIVITY) {
            throw new BusinessException(ErrorCode.NOT_ACTIVITY_INSTANCE);
        }

        LocalDate startedDate = instance.getStartedDate().toLocalDate().minusDays(1);
        LocalDate completedDate = instance.getCompletedDate().toLocalDate().plusDays(1);

        boolean isValidPeriod = targetDate.isAfter(startedDate) && targetDate.isBefore(completedDate);
        if (!isValidPeriod) {
            throw new BusinessException(ErrorCode.NOT_CERTIFICATE_PERIOD);
        }
    }

    public InstancePreviewResponse getInstancePreview(Long instanceId) {
        Instance instance = instanceProvider.findById(instanceId);
        FileResponse fileResponse = filesService.getEncodedFile(instance.getFiles());
        return InstancePreviewResponse.createByEntity(instance, fileResponse);
    }

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
        int certificated = certificationProvider.countByStatus(participantId, CERTIFICATED, currentDate);
        int passed = certificationProvider.countByStatus(participantId, PASSED, currentDate);
        return certificated + passed;
    }

    private double getSuccessPercent(int successCount, int currentAttempt) {
        double successPercent = (double) successCount / (double) currentAttempt * 100;
        return Math.round(successPercent * 100 / 100.0);
    }
}