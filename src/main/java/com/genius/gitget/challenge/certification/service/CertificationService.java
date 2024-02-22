package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.global.util.exception.ErrorCode.CERTIFICATION_UNABLE;

import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationInformation;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.RenewRequest;
import com.genius.gitget.challenge.certification.dto.RenewResponse;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.challenge.participantinfo.domain.Participant;
import com.genius.gitget.challenge.participantinfo.service.ParticipantProvider;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationService {
    private final GithubProvider githubProvider;
    private final CertificationProvider certificationProvider;
    private final ParticipantProvider participantProvider;
    private final InstanceService instanceService;


    public List<RenewResponse> getWeekCertification(Long participantId, LocalDate currentDate) {
        List<Certification> certifications = certificationProvider.findByDuration(
                DateUtil.getWeekStartDate(currentDate),
                currentDate,
                participantId);
        Participant participant = participantProvider.findById(participantId);

        int curAttempt = DateUtil.getWeekAttempt(participant.getStartedDate(), currentDate);

        return convertToRenewResponse(certifications, curAttempt);
    }

    public List<RenewResponse> getTotalCertification(Long participantId, LocalDate currentDate) {
        LocalDate startDate = participantProvider.getInstanceStartDate(participantId);

        List<Certification> certifications = certificationProvider.findByDuration(
                startDate, currentDate, participantId);
        int curAttempt = DateUtil.getAttemptCount(startDate, currentDate);

        return convertToRenewResponse(certifications, curAttempt);
    }

    private List<RenewResponse> convertToRenewResponse(List<Certification> certifications, int curAttempt) {
        List<RenewResponse> result = new ArrayList<>();
        Map<Integer, Certification> certificationMap = convertToMap(certifications);

        for (int cur = 1; cur <= curAttempt; cur++) {
            if (certificationMap.containsKey(cur)) {
                result.add(RenewResponse.createSuccess(certificationMap.get(cur)));
                continue;
            }
            result.add(RenewResponse.createFail(cur));
        }

        return result;
    }

    private Map<Integer, Certification> convertToMap(List<Certification> certifications) {
        Map<Integer, Certification> certificationMap = new HashMap<>();

        for (Certification certification : certifications) {
            certificationMap.put(certification.getCurrentAttempt(), certification);
        }
        return certificationMap;
    }

    @Transactional
    public RenewResponse updateCertification(User user, RenewRequest renewRequest) {
        GitHub gitHub = githubProvider.getGithubConnection(user);
        Instance instance = instanceService.findInstanceById(renewRequest.instanceId());
        Participant participant = participantProvider.findByJoinInfo(user.getId(),
                renewRequest.instanceId());

        if (!canCertificate(instance, renewRequest.targetDate())) {
            throw new BusinessException(CERTIFICATION_UNABLE);
        }

        List<String> pullRequests = getPullRequestLink(
                gitHub,
                participant.getRepositoryName(),
                renewRequest.targetDate());

        Certification certification = certificationProvider.findByDate(renewRequest.targetDate(), participant.getId())
                .orElse(certificationProvider.createCertification(participant, renewRequest.targetDate(),
                        pullRequests));

        return RenewResponse.createSuccess(certification);
    }

    private boolean canCertificate(Instance instance, LocalDate targetDate) {
        boolean isValidPeriod = targetDate.isAfter(instance.getStartedDate().toLocalDate()) &&
                targetDate.isBefore(instance.getCompletedDate().toLocalDate());

        return ((instance.getProgress() == Progress.ACTIVITY) && isValidPeriod);
    }

    private List<String> getPullRequestLink(GitHub gitHub, String repositoryName, LocalDate targetDate) {
        List<GHPullRequest> ghPullRequests = githubProvider.getPullRequestByDate(
                        gitHub,
                        repositoryName,
                        targetDate)
                .nextPage();

        return ghPullRequests.stream()
                .map(pr -> pr.getHtmlUrl().toString())
                .toList();
    }

    public CertificationResponse getInstanceInformation(User user, Long instanceId) {
        Instance instance = instanceService.findInstanceById(instanceId);
        Participant participant = participantProvider.findByJoinInfo(user.getId(), instanceId);

        return CertificationResponse.createByEntity(instance, participant.getRepositoryName());
    }

    @Transactional
    public CertificationInformation getCertificationInformation(Instance instance, Participant participant,
                                                                LocalDate targetDate) {
        //성공 인증 개수
        int successCount = certificationProvider.countCertificatedByStatus(participant.getId(), CERTIFICATED,
                targetDate);

        //targetDate 기준 현재 진행 일차
        int currentAttempt = DateUtil.getAttemptCount(instance.getStartedDate().toLocalDate(), targetDate);

        //남은 인증 개수 = 전체 일차 - 오늘 회차
        int totalAttempt = instance.getTotalAttempt();
        int remainAttempt = totalAttempt - currentAttempt;

        return CertificationInformation.builder()
                .successPercent(getSuccessPercent(successCount, currentAttempt))
                .totalAttempt(totalAttempt)
                .currentAttempt(currentAttempt)
                .pointPerPerson(instance.getPointPerPerson())
                .successCount(successCount)
                .failureCount(currentAttempt - successCount)
                .remainCount(remainAttempt)
                .build();
    }

    private double getSuccessPercent(int successCount, int currentAttempt) {
        double successPercent = (double) successCount / (double) currentAttempt * 100;
        return Math.round(successPercent * 100 / 100.0);
    }
}