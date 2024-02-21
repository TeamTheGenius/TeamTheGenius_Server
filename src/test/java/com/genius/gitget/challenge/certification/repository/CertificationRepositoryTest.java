package com.genius.gitget.challenge.certification.repository;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.Participant;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class CertificationRepositoryTest {
    @Autowired
    CertificationRepository certificationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    ParticipantRepository participantRepository;

    @Test
    @DisplayName("Certification 객체를 만들어서 저장할 수 있다.")
    public void should() {
        //given
        LocalDate certificatedDate = LocalDate.of(2024, 2, 1);
        String certificationLinks = "https://test.com";
        Participant savedParticipant = getSavedParticipant(getSavedUser(), getSavedInstance());

        //when
        Certification savedCertification = getSavedCertification(NOT_YET, certificatedDate, certificationLinks,
                savedParticipant);

        //then
        assertThat(savedCertification.getCertificationStatus()).isEqualTo(NOT_YET);
        assertThat(savedCertification.getCertificatedAt()).isEqualTo(certificatedDate);
        assertThat(savedCertification.getCertificationLinks()).isEqualTo(certificationLinks);
    }

    @Test
    @DisplayName("인증 일자가 특정 기간에 포함된 Certification 객체들을 찾을 수 있다.")
    public void should_returnCertifications_byDuration() {
        //given
        String certificationLinks = "https://test.com";
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 4);
        Participant participant = getSavedParticipant(getSavedUser(), getSavedInstance());

        //when
        getSavedCertification(NOT_YET, startDate, certificationLinks, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), certificationLinks, participant);
        getSavedCertification(CERTIFICATED, endDate.minusDays(1), certificationLinks, participant);
        getSavedCertification(CERTIFICATED, endDate, certificationLinks, participant);

        List<Certification> certifications = certificationRepository.findByDuration(startDate, endDate,
                participant.getId());

        //then
        assertThat(certifications.size()).isEqualTo(4);
    }

    private Certification getSavedCertification(CertificateStatus status, LocalDate certificatedAt,
                                                String certificationLink, Participant participant) {
        Certification certification = Certification.builder()
                .certificationStatus(status)
                .certificatedAt(certificatedAt)
                .certificationLinks(certificationLink)
                .build();
        certification.setParticipant(participant);
        return certificationRepository.save(certification);
    }

    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("identifier")
                        .role(Role.USER)
                        .build()
        );
    }

    private Instance getSavedInstance() {
        return instanceRepository.save(
                Instance.builder()
                        .progress(Progress.ACTIVITY)
                        .build()
        );
    }

    private Participant getSavedParticipant(User user, Instance instance) {
        Participant participant = Participant.builder()
                .joinStatus(JoinStatus.YES)
                .build();
        participant.setUserAndInstance(user, instance);
        return participantRepository.save(participant);
    }
}