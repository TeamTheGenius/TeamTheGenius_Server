package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.RenewRequest;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.participantinfo.domain.Participant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPullRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationProvider {
    private final CertificationRepository certificationRepository;


    public List<Certification> findByDuration(LocalDate startDate, LocalDate endDate, Long participantId) {
        return certificationRepository.findByDuration(startDate, endDate, participantId);
    }

    public Optional<Certification> findByDate(LocalDate targetDate, Long participantId) {
        return certificationRepository.findByDate(targetDate, participantId);
    }

    public int countCertificatedByStatus(Long participantId, CertificateStatus certificateStatus,
                                         LocalDate targetDate) {
        return certificationRepository.findByStatus(participantId, certificateStatus, targetDate).size();
    }

    @Transactional
    public Certification save(Certification certification) {
        return certificationRepository.save(certification);
    }

    //TODO: 이름을 좀 더 직관적으로 만들 수 있지 않을까? 역할을 분리해야 하나?
    @Transactional
    public Certification createCertification(Participant participant,
                                             RenewRequest renewRequest,
                                             List<GHPullRequest> ghPullRequests) {
        LocalDate targetDate = renewRequest.targetDate();
        int attempt = DateUtil.getAttemptCount(participant.getStartedDate(), targetDate);

        Certification certification = findByDate(targetDate, participant.getId())
                .orElse(Certification.builder()
                        .currentAttempt(attempt)
                        .certificatedAt(renewRequest.targetDate())
                        .certificationStatus(getCertificateStatus(ghPullRequests))
                        .certificationLinks(getPrLinks(ghPullRequests))
                        .build());

        certification.setParticipant(participant);

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
}
