package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.CertificationStatus;
import com.genius.gitget.challenge.certification.dto.RenewRequest;
import com.genius.gitget.challenge.certification.dto.RenewResponse;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.service.ParticipantInfoService;
import com.genius.gitget.challenge.user.domain.User;
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

//TODO: 일반 CertificationService와 실제로 컨트롤러에 이용할 Service 분리하기
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationService {
    private final GithubProvider githubProvider;
    private final InstanceService instanceService;
    private final ParticipantInfoService participantInfoService;
    private final CertificationRepository certificationRepository;


    public List<RenewResponse> getWeekCertification(Long participantInfoId, LocalDate currentDate) {
        LocalDate startDate = currentDate.minusDays(currentDate.getDayOfWeek().ordinal());
        List<Certification> certifications = certificationRepository.findByDuration(
                startDate, currentDate, participantInfoId);
        int curAttempt = DateUtil.getDiffBetweenDate(startDate, currentDate);

        return convertToRenewResponse(certifications, curAttempt);
    }

    public List<RenewResponse> getTotalCertification(Long participantInfoId, LocalDate currentDate) {
        Instance instance = participantInfoService.getInstanceById(participantInfoId);
        LocalDate startDate = instance.getStartedDate().toLocalDate();

        List<Certification> certifications = certificationRepository.findByDuration(
                startDate, currentDate, participantInfoId);
        int curAttempt = DateUtil.getDiffBetweenDate(startDate, currentDate);

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
        ParticipantInfo participantInfo = participantInfoService.findByJoinInfo(user.getId(),
                renewRequest.instanceId());

        List<GHPullRequest> ghPullRequests = githubProvider.getPullRequestByDate(
                        gitHub,
                        participantInfo.getRepositoryName(),
                        renewRequest.targetDate())
                .nextPage();

        Certification certification = createCertification(
                participantInfo,
                renewRequest,
                ghPullRequests);

        return RenewResponse.createSuccess(certification);
    }

    private Certification createCertification(ParticipantInfo participantInfo,
                                              RenewRequest renewRequest,
                                              List<GHPullRequest> ghPullRequests) {
        int attempt = DateUtil.getDiffBetweenDate(participantInfo.getStartedDate(),
                renewRequest.targetDate());

        Certification certification = certificationRepository.findByDate(
                        renewRequest.targetDate(),
                        participantInfo.getId())
                .orElse(Certification.builder()
                        .currentAttempt(attempt)
                        .certificatedAt(renewRequest.targetDate())
                        .certificationLinks(getPrLinks(ghPullRequests))
                        .certificationStatus(getCertificateStatus(ghPullRequests))
                        .build());
        certification.setParticipantInfo(participantInfo);
        return certificationRepository.save(certification);
    }

    private String getPrLinks(List<GHPullRequest> ghPullRequests) {
        StringBuilder prLinkBuilder = new StringBuilder();
        for (GHPullRequest pullRequest : ghPullRequests) {
            prLinkBuilder.append(pullRequest.getHtmlUrl().toString());
            prLinkBuilder.append(",");
        }
        return prLinkBuilder.toString();
    }

    private CertificateStatus getCertificateStatus(List<GHPullRequest> ghPullRequests) {
        if (ghPullRequests.isEmpty()) {
            return CertificateStatus.NOT_YET;
        }
        return CERTIFICATED;
    }

    public CertificationResponse getCertificationInformation(User user, Long instanceId) {
        Instance instance = instanceService.findInstanceById(instanceId);
        ParticipantInfo participantInfo = participantInfoService.findByJoinInfo(user.getId(),
                instanceId);

        return CertificationResponse.createByEntity(instance, participantInfo.getRepositoryName());
    }

    @Transactional
    public CertificationStatus getCertificationStatus(Instance instance, ParticipantInfo participantInfo,
                                                      LocalDate targetDate) {
        //성공 인증 개수
        int successCount = certificationRepository.findByStatus(
                        participantInfo.getId(), CERTIFICATED, targetDate)
                .size();
        //실패 인증 개수
        int failureCount = certificationRepository.findByStatus(
                        participantInfo.getId(), NOT_YET, targetDate)
                .size();

        //targetDate 기준 현재 진행 일차
        int currentAttempt = DateUtil.getDiffBetweenDate(instance.getStartedDate().toLocalDate(), targetDate);

        //남은 인증 개수 = 전체 일차 - 오늘 회차
        int totalAttempt = instance.getTotalAttempt();
        int remainAttempt = totalAttempt - currentAttempt;

        //성공 퍼센트
        double successPercent = (double) successCount / (double) currentAttempt * 100;
        double roundedPercent = Math.round(successPercent * 100 / 100.0);

        return CertificationStatus.builder()
                .successPercent(roundedPercent)
                .totalAttempt(totalAttempt)
                .currentAttempt(currentAttempt)
                .pointPerPerson(instance.getPointPerPerson())
                .successCount(successCount)
                .failureCount(failureCount)
                .remainCount(remainAttempt)
                .build();
    }
}
