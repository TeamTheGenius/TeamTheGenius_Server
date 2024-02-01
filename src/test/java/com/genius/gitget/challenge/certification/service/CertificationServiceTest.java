package com.genius.gitget.challenge.certification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
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

    @Value("${github.personalKey}")
    private String personalKey;

    @Value("${github.githubId}")
    private String githubId;

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
        String encrypted = encryptUtil.encryptPersonalToken(personalKey);

        //when & then
        assertThatThrownBy(() -> certificationService.registerGithubPersonalToken(user, personalKey))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.GITHUB_ID_INCORRECT.getMessage());
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
}