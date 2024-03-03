package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static com.genius.gitget.global.util.exception.ErrorCode.CERTIFICATION_UNABLE;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_TOKEN_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationInformation;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.InstancePreviewResponse;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles({"github"})
class CertificationServiceTest {
    @Autowired
    private CertificationService certificationService;
    @Autowired
    private GithubService githubService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private CertificationRepository certificationRepository;

    @Value("${github.personalKey}")
    private String personalKey;

    @Value("${github.githubId}")
    private String githubId;

    @Value("${github.repository}")
    private String targetRepo;


    @Test
    @DisplayName("사용자가 연결한 레포지토리에 특정 날짜의 PR이 있으면 인증으로 간주한다")
    public void should_certificate_when_prExist() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        getParticipantInfo(user, instance);
        githubService.registerGithubPersonalToken(user, personalKey);

        LocalDate targetDate = LocalDate.of(2024, 2, 5);

        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(targetDate)
                .build();
        instance.updateProgress(Progress.ACTIVITY);

        //when
        CertificationResponse certificationResponse = certificationService.updateCertification(user,
                certificationRequest);
        Certification certification = certificationRepository.findById(certificationResponse.certificationId())
                .get();

        //then
        assertThat(certification.getId()).isEqualTo(certificationResponse.certificationId());
        assertThat(certificationResponse.certificateStatus()).isEqualTo(CERTIFICATED);
        assertThat(certificationResponse.certificatedAt()).isEqualTo(targetDate);
        assertThat(certificationResponse.prCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("인증을 시도한 날짜가 챌린지의 진행 기간과 겹치지 않는다면 예외를 발생한다.")
    public void should_throwException_when_progressIsNotActivity() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        getParticipantInfo(user, instance);
        githubService.registerGithubPersonalToken(user, personalKey);

        LocalDate targetDate = LocalDate.of(2024, 3, 6);

        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(targetDate)
                .build();
        instance.updateProgress(Progress.ACTIVITY);

        //when && then
        assertThatThrownBy(() -> certificationService.updateCertification(user, certificationRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CERTIFICATION_UNABLE.getMessage());
    }

    @Test
    @DisplayName("사용자가 연결한 레포지토리에 특정 날짜의 PR이 존재하지 않으면 인증이 아직 안된 것으로 간주한다.")
    public void should_notCertificate_when_prNotExist() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        getParticipantInfo(user, instance);
        githubService.registerGithubPersonalToken(user, personalKey);

        LocalDate targetDate = LocalDate.of(2024, 2, 6);

        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(targetDate)
                .build();
        instance.updateProgress(Progress.ACTIVITY);

        //when
        CertificationResponse certificationResponse = certificationService.updateCertification(user,
                certificationRequest);
        Certification certification = certificationRepository.findById(certificationResponse.certificationId())
                .get();

        //then
        assertThat(certification.getId()).isEqualTo(certificationResponse.certificationId());
        assertThat(certificationResponse.certificateStatus()).isEqualTo(CertificateStatus.NOT_YET);
        assertThat(certificationResponse.certificatedAt()).isEqualTo(targetDate);
        assertThat(certificationResponse.prCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("github를 통해 public repository 정보들을 받아올 수 있다.")
    public void should_returnRepositoryList_when_passGitHubToken() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        githubService.registerGithubPersonalToken(user, personalKey);

        //when
        List<String> repositoryList = githubService.getPublicRepositories(user);

        //then
        assertThat(repositoryList.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("repository 정보를 불러올 때 github token이 제대로 설정되어있지 않다면 예외를 발생해야 한다.")
    public void should_throwException_when_loadRepository() {
        //given
        User user = getSavedUser(githubId);

        //when & then
        assertThatThrownBy(() -> githubService.getPublicRepositories(user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("특정 사용자가 일주일 간 인증한 현황들을 받아올 수 있다.")
    public void should_returnCertifications_when_passDuration() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 2, 3);
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 4);
        Participant participant = getParticipantInfo(getSavedUser(githubId), getSavedInstance());

        //when
        getSavedCertification(NOT_YET, startDate, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, endDate.minusDays(1), participant);
        getSavedCertification(CERTIFICATED, endDate, participant);

        List<CertificationResponse> weekCertification = certificationService.getWeekCertification(
                participant.getId(), currentDate);

        //then
        assertThat(weekCertification.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("특정 사용자가 일주일 간 인증한 현황들을 받아올 때, 더미 데이터를 포함하여 연속적인 데이터로 받아올 수 있다.")
    public void should_returnList_when_dataIsNotContinuous() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 29);
        LocalDate currentDate = LocalDate.of(2024, 2, 8);

        Participant participant = getParticipantInfo(getSavedUser(githubId), getSavedInstance());

        //when
        getSavedCertification(NOT_YET, startDate, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(4), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(6), participant);

        List<CertificationResponse> weekCertification = certificationService.getWeekCertification(
                participant.getId(), currentDate);

        //then
        assertThat(weekCertification.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("현재 일자까지의 인증 현황들을 받아올 수 있다.")
    public void should_returnList_when_passDate() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 29);
        LocalDate currentDate = LocalDate.of(2024, 2, 8);

        Participant participant = getParticipantInfo(getSavedUser(githubId), getSavedInstance());

        //when
        getSavedCertification(NOT_YET, startDate, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(4), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(6), participant);

        List<CertificationResponse> certification = certificationService.getTotalCertification(participant.getId(),
                currentDate);

        //then
        assertThat(certification.size()).isEqualTo(8);
    }

    @Test
    @DisplayName("사용자가 참여한 챌린지에 대한 상세 정보를 받을 수 있다.")
    public void should_returnDetailInfo_when_participate() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getParticipantInfo(user, instance);

        //when
        InstancePreviewResponse instancePreviewResponse = certificationService.getInstancePreview(instance.getId());

        //then
        assertThat(instancePreviewResponse.instanceId()).isEqualTo(instance.getId());
    }

    @Test
    @DisplayName("사용자가 참여한 챌린지에 대해 전반적인 현황을 받을 수 있다.")
    public void should_getInformation_when_participate() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 29);
        LocalDate targetDate = LocalDate.of(2024, 2, 8);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getParticipantInfo(user, instance);

        //when
        getSavedCertification(NOT_YET, startDate, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(4), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(6), participant);
        CertificationInformation information = certificationService.getCertificationInformation(instance,
                participant, targetDate);

        //then
        assertThat(information.pointPerPerson()).isEqualTo(instance.getPointPerPerson());
        assertThat(information.remainCount()).isEqualTo(information.totalAttempt() - information.currentAttempt());
        assertThat(information.totalAttempt()).isEqualTo(29);
        assertThat(information.currentAttempt()).isEqualTo(8);
        assertThat(information.successCount()).isEqualTo(3);
        assertThat(information.failureCount()).isEqualTo(information.currentAttempt() - information.successCount());
    }


    private User getSavedUser(String githubId) {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier(githubId)
                        .information("information")
                        .tags("BE,FE")
                        .build()
        );
    }

    private Instance getSavedInstance() {
        return instanceRepository.save(
                Instance.builder()
                        .progress(Progress.PREACTIVITY)
                        .startedDate(LocalDateTime.of(2024, 2, 1, 11, 3))
                        .completedDate(LocalDateTime.of(2024, 2, 29, 23, 59))
                        .build()
        );
    }

    private Participant getParticipantInfo(User user, Instance instance) {
        Participant participant = participantRepository.save(
                Participant.builder()
                        .joinResult(JoinResult.PROCESSING)
                        .joinStatus(JoinStatus.YES)
                        .build()
        );
        participant.setUserAndInstance(user, instance);
        participant.updateRepository(targetRepo);

        return participant;
    }


    private Certification getSavedCertification(CertificateStatus status, LocalDate certificatedAt,
                                                Participant participant) {
        int attempt = DateUtil.getAttemptCount(participant.getStartedDate(), certificatedAt);
        Certification certification = Certification.builder()
                .certificationStatus(status)
                .currentAttempt(attempt)
                .certificatedAt(certificatedAt)
                .certificationLinks("certificationLink")
                .build();
        certification.setParticipant(participant);
        return certificationRepository.save(certification);
    }
}