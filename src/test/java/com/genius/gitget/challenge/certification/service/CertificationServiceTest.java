package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_TOKEN_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.PARTICIPANT_INFO_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.PullRequestResponse;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantInfoRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import java.time.LocalDate;
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
    private ParticipantInfoRepository participantInfoRepository;
    @Autowired
    private CertificationRepository certificationRepository;

    @Value("${github.personalKey}")
    private String personalKey;

    @Value("${github.githubId}")
    private String githubId;

    @Value("${github.repository}")
    private String targetRepo;

    @Test
    @DisplayName("사용자의 github token이 저장되어있지 않을 때 예외가 발생해야 한다.")
    public void should_throwException_when_githubTokenNotSaved() {
        //given
        LocalDate targetDate = LocalDate.of(2024, 2, 5);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();

        //when & then
        assertThatThrownBy(() -> certificationService.getPullRequestListByDate(user, instance.getId(), targetDate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("repository가 등록되어있지 않을 때 예외가 발생해야 한다.")
    public void should_throwException_when_repositoryNotRegistered() {
        //given
        LocalDate targetDate = LocalDate.of(2024, 2, 5);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        githubService.registerGithubPersonalToken(user, personalKey);

        //when & then
        assertThatThrownBy(() -> certificationService.getPullRequestListByDate(user, instance.getId(), targetDate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(PARTICIPANT_INFO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("특정 일자에 PR이 존재하지 않는다면 빈 리스트를 반환한다.")
    public void should_returnEmptyList_when_prNotExist() throws IOException {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        githubService.registerGithubPersonalToken(user, personalKey);
        githubService.registerRepository(user, instance.getId(), targetRepo);

        LocalDate targetDate = LocalDate.of(2024, 1, 4);

        //when
        List<PullRequestResponse> pullRequestResponses = certificationService.getPullRequestListByDate(
                user, instance.getId(), targetDate);

        //then
        assertThat(pullRequestResponses.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자가 연결한 레포지토리에 특정 날짜의 PR이 있으면 인증으로 간주한다")
    public void should_certificate_when_prExist() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        githubService.registerGithubPersonalToken(user, personalKey);
        githubService.registerRepository(user, instance.getId(), targetRepo);

        LocalDate targetDate = LocalDate.of(2024, 2, 5);

        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(targetDate)
                .build();

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
    @DisplayName("사용자가 연결한 레포지토리에 특정 날짜의 PR이 존재하지 않으면 인증이 아직 안된 것으로 간주한다.")
    public void should_notCertificate_when_prNotExist() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        githubService.registerGithubPersonalToken(user, personalKey);
        githubService.registerRepository(user, instance.getId(), targetRepo);

        LocalDate targetDate = LocalDate.of(2024, 2, 6);

        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(targetDate)
                .build();

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
    @DisplayName("특정 사용자가 인증한 특정 기간 내에 인증된 인증 객체들을 받아올 수 있다.")
    public void should_returnCertifications_when_passDuration() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 2, 3);
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 4);
        ParticipantInfo participantInfo = getParticipantInfo(getSavedUser(githubId), getSavedInstance());

        //when
        getSavedCertification(NOT_YET, startDate, participantInfo);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participantInfo);
        getSavedCertification(CERTIFICATED, endDate.minusDays(1), participantInfo);
        getSavedCertification(CERTIFICATED, endDate, participantInfo);

        List<CertificationResponse> weekCertification = certificationService.getWeekCertification(
                participantInfo.getId(), currentDate);

        //then
        assertThat(weekCertification.size()).isEqualTo(3);
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
                        .build()
        );
    }

    private ParticipantInfo getParticipantInfo(User user, Instance instance) {
        ParticipantInfo participantInfo = participantInfoRepository.save(
                ParticipantInfo.builder()
                        .joinResult(JoinResult.PROCESSING)
                        .joinStatus(JoinStatus.YES)
                        .build()
        );
        participantInfo.setUserAndInstance(user, instance);

        return participantInfo;
    }

    @Test
    @DisplayName("특정 레포지토리에 특정 날짜에 생성된 PR 목록을 불러올 수 있다.")
    public void should_loadPRList_when_tryJoin() throws IOException {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        githubService.registerGithubPersonalToken(user, personalKey);
        githubService.registerRepository(user, instance.getId(), targetRepo);

        LocalDate targetDate = LocalDate.of(2024, 2, 5);

        //when
        List<PullRequestResponse> pullRequestResponses = certificationService.getPullRequestListByDate(
                user, instance.getId(), targetDate);

        //then
        assertThat(pullRequestResponses.size()).isEqualTo(1);
        log.info(pullRequestResponses.get(0).toString());
    }

    private Certification getSavedCertification(CertificateStatus status, LocalDate certificatedAt,
                                                ParticipantInfo participantInfo) {
        Certification certification = Certification.builder()
                .certificationStatus(status)
                .certificatedAt(certificatedAt)
                .certificationLinks("certificationLink")
                .build();
        certification.setParticipantInfo(participantInfo);
        return certificationRepository.save(certification);
    }
}