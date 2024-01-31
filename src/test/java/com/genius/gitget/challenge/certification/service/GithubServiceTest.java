package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_ID_INCORRECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GitHub;
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

    @Value("${github.githubId}")
    private String githubId;

    @Value("${github.repository}")
    private String repository;

    @Test
    @DisplayName("정상적인 github token을 전달받았을 때, API를 통해 GitHub 객체를 반환받을 수 있다.")
    public void should_returnGitHubInstance_when_passValidToken() {
        //given

        //when
        GitHub gitHub = githubService.getGithubConnection(personalKey);

        //then
        assertThat(gitHub).isNotNull();
    }

    @Test
    @DisplayName("github token을 전달받았을 때, 사용자가 소셜로그인할 때 사용했던 깃허브 계정 아이디와 일치한다면 연결 성공으로 간주한다.")
    public void should_checkConnection_when_passPersonalToken() {
        //given
        GitHub gitHub = getGitHub();

        //when
        githubService.validateGithubConnection(gitHub, githubId);
    }

    @Test
    @DisplayName("github token을 전달받았을 때, 소셜로그인 깃허브 계정 아이디와 일치하지 않는다면 예외가 발생한다.")
    public void should_throwException_when_idIncorrect() {
        //given
        GitHub gitHub = getGitHub();
        String githubId = "fake Id";

        //when & then
        assertThatThrownBy(() -> githubService.validateGithubConnection(gitHub, githubId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_ID_INCORRECT.getMessage());
    }

    @Test
    @DisplayName("특정 사용자의 특정 repository가 연결됨을 검증할 수 있다.")
    public void should_findRepository_when_passToken() throws IOException {
        // given
        GitHub gitHub = getGitHub();

        //when & then
        githubService.validateGithubRepository(gitHub, githubId + "/" + repository);
    }

    @Test
    @DisplayName("전달받은 Repository명이 명확하지 않는다면 예외가 발생한다.")
    public void should_throwException_when_repositoryNameInvalid() {
        //given
        GitHub gitHub = getGitHub();
        String repositoryName = "fake repository";

        //when & then
        assertThatThrownBy(() -> githubService.validateGithubRepository(gitHub, repositoryName))
                .isInstanceOf(BusinessException.class);
    }

    private GitHub getGitHub() {
        return githubService.getGithubConnection(personalKey);
    }
}