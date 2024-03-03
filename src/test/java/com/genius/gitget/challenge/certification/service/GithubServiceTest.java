package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_REPOSITORY_INCORRECT;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_TOKEN_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.github.PullRequestResponse;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.EncryptUtil;
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
import com.genius.gitget.global.util.exception.ErrorCode;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class GithubServiceTest {
    @Autowired
    private EncryptUtil encryptUtil;
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
    @DisplayName("Github personal access token 연결에 이상이 없다면, 암호화하여 User 엔티티에 저장한다.")
    public void should_updateTokenInfo_when_tokenValid() {
        //given
        User user = getSavedUser(githubId);
        String encrypted = encryptUtil.encrypt(personalKey);

        //when
        githubService.registerGithubPersonalToken(user, personalKey);
        User updatedUser = userRepository.findByIdentifier(githubId).get();

        //then
        assertThat(updatedUser.getGithubToken()).isEqualTo(encrypted);
    }

    @Test
    @DisplayName("Github personal access token과 소셜로그인 시의 계정이 일치하지 않으면 예외를 발생시킨다.")
    public void should_throwException_when_accountIncorrect() {
        //given
        User user = getSavedUser("incorrect Id");
        encryptUtil.encrypt(personalKey);

        //when & then
        assertThatThrownBy(() -> githubService.registerGithubPersonalToken(user, personalKey))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.GITHUB_ID_INCORRECT.getMessage());
    }

    @Test
    @DisplayName("repository 이름을 전달했을 때, 해당 깃허브 계정에 레포지토리가 있어야 한다.")
    public void should_repositoryExist_when_passRepositoryName() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        githubService.registerGithubPersonalToken(user, personalKey);

        //when
        githubService.verifyRepository(user, targetRepo);

    }

    @Test
    @DisplayName("repository 등록 시, 해당 레포지토리가 없다면 예외가 발생해야 한다.")
    public void should_throw_Exception_when_thereIsNoRepository() {
        //given
        String fakeRepositoryName = "Fake";
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        githubService.registerGithubPersonalToken(user, personalKey);

        //when & then
        assertThatThrownBy(() -> githubService.verifyRepository(user, fakeRepositoryName))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_REPOSITORY_INCORRECT.getMessage());
    }

    @Test
    @DisplayName("repository 등록 시, 사용자에게 Github token이 저장되어있지 않다면 예외가 발생해야 한다.")
    public void should_throwException_when_userDontHaveToken() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getParticipantInfo(user, instance);

        //when & then
        assertThatThrownBy(() -> githubService.verifyRepository(user, targetRepo))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("특정 일자에 PR이 존재하지 않는다면 빈 리스트를 반환한다.")
    public void should_returnEmptyList_when_prNotExist() throws IOException {
        //given
        User user = getSavedUser(githubId);
        githubService.registerGithubPersonalToken(user, personalKey);

        LocalDate targetDate = LocalDate.of(2024, 1, 4);

        //when
        List<PullRequestResponse> pullRequestResponses = githubService.getPullRequestListByDate(
                user, targetRepo, targetDate);

        //then
        assertThat(pullRequestResponses.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자의 github token이 저장되어있지 않을 때 예외가 발생해야 한다.")
    public void should_throwException_when_githubTokenNotSaved() {
        //given
        LocalDate targetDate = LocalDate.of(2024, 2, 5);
        User user = getSavedUser(githubId);

        //when & then
        assertThatThrownBy(() -> githubService.getPullRequestListByDate(user, targetRepo, targetDate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("repository가 등록되어있지 않을 때 예외가 발생해야 한다.")
    public void should_throwException_when_repositoryNotRegistered() {
        //given
        LocalDate targetDate = LocalDate.of(2024, 2, 5);
        User user = getSavedUser(githubId);
        String fakeRepo = "fake Repo";
        githubService.registerGithubPersonalToken(user, personalKey);

        //when & then
        assertThatThrownBy(() -> githubService.getPullRequestListByDate(user, fakeRepo, targetDate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_REPOSITORY_INCORRECT.getMessage());
    }

    @Test
    @DisplayName("특정 레포지토리에 특정 날짜에 생성된 PR 목록을 불러올 수 있다.")
    public void should_loadPRList_when_tryJoin() throws IOException {
        //given
        User user = getSavedUser(githubId);
        githubService.registerGithubPersonalToken(user, personalKey);

        LocalDate targetDate = LocalDate.of(2024, 2, 5);

        //when
        List<PullRequestResponse> pullRequestResponses = githubService.getPullRequestListByDate(
                user, targetRepo, targetDate);

        //then
        assertThat(pullRequestResponses.size()).isEqualTo(1);
        log.info(pullRequestResponses.get(0).toString());
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

    private Participant getParticipantInfo(User user, Instance instance) {
        Participant participant = participantRepository.save(
                Participant.builder()
                        .joinResult(JoinResult.PROCESSING)
                        .joinStatus(JoinStatus.YES)
                        .build()
        );
        participant.setUserAndInstance(user, instance);

        return participant;
    }

    private Certification getSavedCertification(CertificateStatus status, LocalDate certificatedAt,
                                                Participant participant) {
        Certification certification = Certification.builder()
                .certificationStatus(status)
                .certificatedAt(certificatedAt)
                .certificationLinks("certificationLink")
                .build();
        certification.setParticipant(participant);
        return certificationRepository.save(certification);
    }
}