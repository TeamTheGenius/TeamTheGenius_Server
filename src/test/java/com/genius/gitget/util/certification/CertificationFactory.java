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

    public static Certification createNotYet(Participant participant, LocalDate certificatedAt) {
        int attempt = DateUtil.getAttemptCount(participant.getStartedDate(), certificatedAt);
        Certification certification = Certification.builder()
                .certificationStatus(CertificateStatus.NOT_YET)
                .currentAttempt(attempt)
                .certificatedAt(certificatedAt)
                .certificationLinks(null)
                .build();
        certification.setParticipant(participant);
        return certification;
    }

    public static Certification createCertificated(Participant participant, LocalDate certificatedAt) {
        int attempt = DateUtil.getAttemptCount(participant.getStartedDate(), certificatedAt);
        Certification certification = Certification.builder()
                .certificationStatus(CertificateStatus.CERTIFICATED)
                .currentAttempt(attempt)
                .certificatedAt(certificatedAt)
                .certificationLinks("certificationLink")
                .build();
        certification.setParticipant(participant);
        return certification;
    }

    public static Certification createPassed(Participant participant, LocalDate certificatedAt) {
        int attempt = DateUtil.getAttemptCount(participant.getStartedDate(), certificatedAt);
        Certification certification = Certification.builder()
                .certificationStatus(CertificateStatus.PASSED)
                .currentAttempt(attempt)
                .certificatedAt(certificatedAt)
                .certificationLinks(null)
                .build();
        certification.setParticipant(participant);
        return certification;
    }
}
