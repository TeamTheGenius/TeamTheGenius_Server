package com.genius.gitget.challenge.certification.domain;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.PASSED;

import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.global.util.domain.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Certification extends BaseTimeEntity {
    @Id
    @Column(name = "certification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    private int currentAttempt;

    private LocalDate certificatedAt;

    private String certificationLinks;

    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'NOT_YET'")
    private CertificateStatus certificationStatus;


    @Builder
    public Certification(int currentAttempt, LocalDate certificatedAt, String certificationLinks,
                         CertificateStatus certificationStatus) {
        this.currentAttempt = currentAttempt;
        this.certificatedAt = certificatedAt;
        this.certificationLinks = certificationLinks;
        this.certificationStatus = certificationStatus;
    }

    public static Certification createPassed(LocalDate certificatedAt) {
        return Certification.builder()
                .certificatedAt(certificatedAt)
                .certificationStatus(PASSED)
                .certificationLinks("")
                .build();
    }

    //=== 비지니스 로직 ===//
    public void update(LocalDate certificatedAt, CertificateStatus status, String certificationLinks) {
        this.certificatedAt = certificatedAt;
        this.certificationStatus = status;
        this.certificationLinks = certificationLinks;
    }

    public void updateToPass(LocalDate certificatedAt) {
        this.certificatedAt = certificatedAt;
        this.certificationStatus = CertificateStatus.PASSED;
        this.certificationLinks = "";
    }


    //=== 연관관계 편의 메서드 ===//
    public void setParticipant(Participant participant) {
        this.participant = participant;
        if (!participant.getCertificationList().contains(this)) {
            participant.getCertificationList().add(this);
        }
    }
}
