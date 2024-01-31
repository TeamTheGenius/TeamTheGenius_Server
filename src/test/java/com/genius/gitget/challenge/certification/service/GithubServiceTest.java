package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_ID_INCORRECT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.global.util.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"github"})
class GithubServiceTest {
    @Autowired
    private GithubService githubService;

    @Value("${github.personalKey}")
    private String personalKey;

    @Test
    @DisplayName("github token을 전달받았을 때, 사용자가 소셜로그인할 때 사용했던 깃허브 계정 아이디와 일치한다면 연결 성공으로 간주한다.")
    public void should_checkConnection_when_passPersonalToken() {
        //given
        String githubId = "SSung023";
        githubService.validateGithubConnection(githubId, personalKey);
    }

    @Test
    @DisplayName("github token을 전달받았을 때, 소셜로그인 깃허브 계정 아이디와 일치하지 않는다면 예외가 발생한다.")
    public void should_throwException_when_idIncorrect() {
        //given
        String githubId = "fake Id";

        //when & then
        assertThatThrownBy(() -> githubService.validateGithubConnection(githubId, personalKey))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_ID_INCORRECT.getMessage());
    }

    @Test
    @DisplayName("github token을 전달받았을 때, token이 유효하지 않는다면 예외가 발생한다.")
    public void should_throwException_when_tokenInvalid() {
        //given
        String githubId = "SSung023";
        String fakeToken = "fake token";

        //when & then
        assertThatThrownBy(() -> githubService.validateGithubConnection(githubId, fakeToken))
                .isInstanceOf(BusinessException.class);
    }
}