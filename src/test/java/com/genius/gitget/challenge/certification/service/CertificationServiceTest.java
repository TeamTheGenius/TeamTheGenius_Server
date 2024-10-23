package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class CertificationServiceTest {
    @Autowired
    private CertificationService certificationService;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("DB에서 특정 기간 내의 인증 객체 리스트들을 받아올 수 있다.")
    public void should_returnList_when_passDuration() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 5);
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);

        getSavedCertification(startDate, CERTIFICATED, "link1", participant);
        getSavedCertification(startDate.plusDays(1), CERTIFICATED, "link1", participant);
        getSavedCertification(endDate.minusDays(1), NOT_YET, null, participant);
        getSavedCertification(endDate.minusDays(2), CERTIFICATED, "link1", participant);

        //when
        List<Certification> certifications = certificationService.findByDuration(startDate, endDate,
                participant.getId());

        //then
        assertThat(certifications.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("특정 일자에 저장된 인증 객체를 받아올 수 있다.")
    public void should_getCertification_when_passDate() {
        //given
        LocalDate targetDate = LocalDate.of(2024, 2, 1);
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);

        //when
        getSavedCertification(targetDate, CERTIFICATED, "link1", participant);
        Optional<Certification> byDate = certificationService.findByDate(targetDate, participant.getId());

        //then
        assertThat(byDate).isPresent();
    }

    @Test
    @DisplayName("특정 기간 이내에 특정 인증 상태인 인증 객체의 개수를 받아올 수 있다.")
    public void should_count_when_passStatus() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 5);
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);

        getSavedCertification(startDate, CERTIFICATED, "link1", participant);
        getSavedCertification(startDate.plusDays(1), CERTIFICATED, "link1", participant);
        getSavedCertification(endDate.minusDays(1), NOT_YET, null, participant);
        getSavedCertification(endDate.minusDays(2), CERTIFICATED, "link1", participant);

        //when
        int certificated = certificationService.countByStatus(participant.getId(), CERTIFICATED,
                endDate);

        //then
        assertThat(certificated).isEqualTo(3);
    }

    @Test
    @DisplayName("사용자가 인증을 생성/갱신할 수 있다.")
    public void should_renewCertification() {
        //given
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        LocalDate targetDate = LocalDate.of(2024, 2, 1);
        List<String> pullRequests = List.of("pr link1", "pr link2");

        //when
        Certification certification = certificationService.createCertificated(participant, targetDate,
                pullRequests);

        //then
        assertThat(certification.getCertificatedAt()).isEqualTo(targetDate);
    }

    @Test
    @DisplayName("인증과 관련된 정보를 전달했을 때, 객체의 정보를 업데이트할 수 있다.")
    public void should_update_when_passInfo() {
        //given
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        LocalDate targetDate = LocalDate.of(2024, 2, 1);
        Certification certification = getSavedCertification(targetDate, NOT_YET, "", participant);
        List<String> pullRequests = List.of("pr link1", "pr link2");

        //when
        Certification updatedCertification = certificationService.update(certification, targetDate, pullRequests);

        //then
        assertThat(updatedCertification.getId()).isEqualTo(certification.getId());
        assertThat(updatedCertification.getCertificatedAt()).isEqualTo(targetDate);
        assertThat(updatedCertification.getCertificationStatus()).isEqualTo(CERTIFICATED);
        assertThat(updatedCertification.getCertificationLinks()).isEqualTo("pr link1,pr link2,");
    }

    private Certification getSavedCertification(LocalDate certificatedAt, CertificateStatus status,
                                                String link, Participant participant) {
        Certification certification = certificationService.save(
                Certification.builder()
                        .certificatedAt(certificatedAt)
                        .certificationStatus(status)
                        .certificationLinks(link)
                        .build()
        );
        certification.setParticipant(participant);
        return certification;
    }

    private Participant getSavedParticipant(User user, Instance instance) {
        Participant participant = participantRepository.save(
                Participant.createDefaultParticipant("repo")
        );
        participant.setUserAndInstance(user, instance);
        return participant;
    }

    private Instance getSavedInstance() {
        return instanceRepository.save(
                Instance.builder()
                        .startedDate(LocalDateTime.of(2024, 2, 1, 0, 0))
                        .pointPerPerson(100)
                        .progress(Progress.PREACTIVITY)
                        .build()
        );
    }

    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("githubId")
                        .information("information")
                        .tags("BE,FE")
                        .build()
        );
    }
}