package com.genius.gitget.challenge.certification.service;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.PullRequestResponse;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.service.ParticipantInfoService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
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
    private final UserService userService;
    private final GithubService githubService;
    private final ParticipantInfoService participantInfoService;
    private final CertificationRepository certificationRepository;
    private final EncryptUtil encryptUtil;


    @Transactional
    public void registerGithubPersonalToken(User user, String githubToken) {
        GitHub gitHub = githubService.getGithubConnection(githubToken);
        githubService.validateGithubConnection(gitHub, user.getIdentifier());

        String encryptedToken = encryptUtil.encryptPersonalToken(githubToken);
        user.updateGithubPersonalToken(encryptedToken);
        userService.save(user);
    }

    @Transactional
    public void registerRepository(User user, Long instanceId, String repository) {
        String githubToken = userService.getGithubToken(user);
        GitHub gitHub = githubService.getGithubConnection(githubToken);

        String repositoryFullName = user.getIdentifier() + "/" + repository;
        githubService.validateGithubRepository(gitHub, repositoryFullName);

        participantInfoService.joinNewInstance(user.getId(), instanceId, repositoryFullName);
    }

    public List<PullRequestResponse> getPullRequestListByDate(User user, Long instanceId, LocalDate targetDate) {
        String githubToken = userService.getGithubToken(user);
        GitHub gitHub = githubService.getGithubConnection(githubToken);

        String repositoryName = participantInfoService.getRepositoryName(user.getId(), instanceId);

        List<GHPullRequest> pullRequest = githubService.getPullRequestByDate(gitHub, repositoryName, targetDate)
                .nextPage();

        return pullRequest.stream()
                .map(PullRequestResponse::create)
                .toList();
    }


    //refactor: 인증 날짜, 사용자 정보, 인스턴스 혹은 참여 정보를 DTO로 만드는 것도 괜찮을 듯
    @Transactional
    public CertificationResponse updateCertification(User user, CertificationRequest certificationRequest) {
        String githubToken = userService.getGithubToken(user);
        GitHub gitHub = githubService.getGithubConnection(githubToken);
        ParticipantInfo participantInfo = participantInfoService.getParticipantInfo(user.getId(),
                certificationRequest.instanceId());

        List<GHPullRequest> ghPullRequests = githubService.getPullRequestByDate(
                        gitHub,
                        participantInfo.getRepositoryName(),
                        certificationRequest.targetDate())
                .nextPage();

        Certification certification = certificationRepository.findCertificationByDate(certificationRequest.targetDate(),
                        participantInfo.getId())
                .orElse(Certification.builder()
                        .userId(user.getId())
                        .instanceId(certificationRequest.instanceId())
                        .certificatedAt(certificationRequest.targetDate())
                        .certificationLinks(getPrLinks(ghPullRequests))
                        .certificationStatus(getCertificateStatus(ghPullRequests))
                        .build());

        certification.setParticipantInfo(participantInfo);
        Certification savedCertification = certificationRepository.save(certification);

        return CertificationResponse.create(savedCertification);
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
