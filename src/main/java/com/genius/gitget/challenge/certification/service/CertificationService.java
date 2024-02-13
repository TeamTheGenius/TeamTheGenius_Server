package com.genius.gitget.challenge.certification.service;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.service.ParticipantInfoService;
import com.genius.gitget.challenge.user.domain.User;
import java.time.LocalDate;
import java.util.List;
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
    private final ParticipantInfoService participantInfoService;
    private final CertificationRepository certificationRepository;


    public List<CertificationResponse> getWeekCertification(Long participantInfoId, LocalDate currentDate) {
        LocalDate startDate = currentDate.minusDays(currentDate.getDayOfWeek().ordinal());
        List<Certification> certifications = certificationRepository.findCertificationByDuration(startDate, currentDate,
                participantInfoId);

        return certifications.stream()
                .map(CertificationResponse::create)
                .toList();
    }

    public List<CertificationResponse> getTotalCertification(Long participantInfoId, LocalDate currentDate) {
        ParticipantInfo participantInfo = participantInfoService.findParticipantInfoById(participantInfoId);
        Instance instance = participantInfo.getInstance();

        List<Certification> certifications = certificationRepository.findCertificationByDuration(
                instance.getStartedDate().toLocalDate(), currentDate, participantInfoId);

        return certifications.stream()
                .map(CertificationResponse::create)
                .toList();
    }

    @Transactional
    public CertificationResponse updateCertification(User user, CertificationRequest certificationRequest) {
        GitHub gitHub = githubProvider.getGithubConnection(user);
        ParticipantInfo participantInfo = participantInfoService.getParticipantInfoByJoinInfo(user.getId(),
                certificationRequest.instanceId());

        List<GHPullRequest> ghPullRequests = githubProvider.getPullRequestByDate(
                        gitHub,
                        participantInfo.getRepositoryName(),
                        certificationRequest.targetDate())
                .nextPage();

        Certification certification = createCertification(
                participantInfo,
                certificationRequest,
                ghPullRequests);

        return CertificationResponse.create(certification);
    }

    private Certification createCertification(ParticipantInfo participantInfo,
                                              CertificationRequest certificationRequest,
                                              List<GHPullRequest> ghPullRequests) {
        int attempt = DateUtil.getCertificationAttempt(participantInfo.getStartedDate(),
                certificationRequest.targetDate());

        Certification certification = certificationRepository.findCertificationByDate(
                        certificationRequest.targetDate(),
                        participantInfo.getId())
                .orElse(Certification.builder()
                        .certificationAttempt(attempt)
                        .certificatedAt(certificationRequest.targetDate())
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
        return CertificateStatus.CERTIFICATED;
    }
}
