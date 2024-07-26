package com.genius.gitget.util.certification;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.participant.domain.Participant;
import java.time.LocalDate;

public class CertificationFactory {
    public static Certification create(CertificateStatus status, LocalDate certificatedAt,
                                       Participant participant) {
        int attempt = DateUtil.getAttemptCount(participant.getStartedDate(), certificatedAt);
        Certification certification = Certification.builder()
                .certificationStatus(status)
                .currentAttempt(attempt)
                .certificatedAt(certificatedAt)
                .certificationLinks("certificationLink")
                .build();
        certification.setParticipant(participant);
        return certification;
    }
}
