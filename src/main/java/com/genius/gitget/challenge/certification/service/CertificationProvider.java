package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.participant.domain.Participant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public Certification update(Certification certification,
                                LocalDate targetDate,
                                List<String> pullRequests) {
        certification.update(
                targetDate, getCertificateStatus(pullRequests), getPrLinks(pullRequests)
        );
        return certification;
    }

    @Transactional
    public Certification createCertification(Participant participant,
                                             LocalDate targetDate,
                                             List<String> pullRequests) {
        int attempt = DateUtil.getAttemptCount(participant.getStartedDate(), targetDate);

        Certification certification = Certification.builder()
                .currentAttempt(attempt)
                .certificatedAt(targetDate)
                .certificationStatus(getCertificateStatus(pullRequests))
                .certificationLinks(getPrLinks(pullRequests))
                .build();

        certification.setParticipant(participant);

        return certificationRepository.save(certification);
    }

    private String getPrLinks(List<String> pullRequests) {
        StringBuilder prLinkBuilder = new StringBuilder();
        for (String pullRequest : pullRequests) {
            prLinkBuilder.append(pullRequest);
            prLinkBuilder.append(",");
        }
        return prLinkBuilder.toString();
    }

    private CertificateStatus getCertificateStatus(List<String> pullRequests) {
        if (pullRequests.isEmpty()) {
            return NOT_YET;
        }
        return CERTIFICATED;
    }
}
