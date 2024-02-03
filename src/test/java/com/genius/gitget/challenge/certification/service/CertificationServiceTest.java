package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_REPOSITORY_INCORRECT;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_TOKEN_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.util.EncryptUtil;
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
import com.genius.gitget.global.util.exception.ErrorCode;
import java.util.Optional;
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
    private EncryptUtil encryptUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private ParticipantInfoRepository participantInfoRepository;

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
        String encrypted = encryptUtil.encryptPersonalToken(personalKey);

        //when
        certificationService.registerGithubPersonalToken(user, personalKey);

        //then
        assertThat(user.getGithubToken()).isEqualTo(encrypted);
    }

    @Test
    @DisplayName("Github personal access token과 소셜로그인 시의 계정이 일치하지 않으면 예외를 발생시킨다.")
    public void should_throwException_when_accountIncorrect() {
        //given
        User user = getSavedUser("incorrect Id");
        encryptUtil.encryptPersonalToken(personalKey);

        //when & then
        assertThatThrownBy(() -> certificationService.registerGithubPersonalToken(user, personalKey))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.GITHUB_ID_INCORRECT.getMessage());
    }

    @Test
    @DisplayName("repository 이름을 전달했을 때, 해당 깃허브 계정에 레포지토리가 있어야 한다.")
    public void should_repositoryExist_when_passRepositoryName() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        certificationService.registerGithubPersonalToken(user, personalKey);

        //when
        certificationService.registerRepository(user, instance.getId(), targetRepo);

    }

    @Test
    @DisplayName("repository 등록 시, 해당 레포지토리가 없다면 예외가 발생해야 한다.")
    public void should_throw_Exception_when_thereIsNoRepository() {
        //given
        String fakeRepositoryName = "Fake";
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        certificationService.registerGithubPersonalToken(user, personalKey);

        //when & then
        assertThatThrownBy(() -> certificationService.registerRepository(user, instance.getId(), fakeRepositoryName))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_REPOSITORY_INCORRECT.getMessage());
    }

    @Test
    @DisplayName("repository 등록 시, 사용자에게 Github token이 저장되어있지 않다면 예외가 발생해야 한다.")
    public void should_throwException_when_userDontHaveToken() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        ParticipantInfo participantInfo = getParticipantInfo(user, instance);

        //when & then
        assertThatThrownBy(() -> certificationService.registerRepository(user, instance.getId(), targetRepo))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("repository 등록 시, 인스턴스 id가 제대로 전달되지 않았다면 예외가 발생해야 한다.")
    public void should_throwException_when_instanceIdInvalid() {
        //given
        User user = getSavedUser(githubId);
        certificationService.registerGithubPersonalToken(user, personalKey);

        //when & then
        assertThatThrownBy(() -> certificationService.registerRepository(user, 1L, targetRepo))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(INSTANCE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("repository 등록 시, ParticipantInfo 엔티티를 등록할 수 있다,")
    public void should_saveParticipant_when_registerRepository() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        certificationService.registerGithubPersonalToken(user, personalKey);

        //when
        certificationService.registerRepository(user, instance.getId(), targetRepo);

        //then
        Optional<ParticipantInfo> participantInfo = participantInfoRepository.findBy(user.getId(), instance.getId());
        assertThat(participantInfo).isNotEmpty();
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
}