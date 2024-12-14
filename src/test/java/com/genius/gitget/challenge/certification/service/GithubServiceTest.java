package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_ID_INCORRECT;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_REPOSITORY_INCORRECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
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

    @Value("${github.yeon-personalKey}")
    private String personalKey;

    @Value("${github.yeon-githubId}")
    private String githubId;

    @Value("${github.yeon-repository}")
    private String repository;

    @Test
    @DisplayName("정상적인 github token을 전달받았을 때, API를 통해 GitHub 객체를 반환받을 수 있다.")
    public void should_returnGitHubInstance_when_passValidToken() {
        //given

        //when
        GitHub gitHub = githubService.getGithubConnection(personalKey).join();

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

    @Test
    @DisplayName("해당 레포지토리에 있는 PR을 확인할 수 있다.")
    public void should_checkPR_when_validRepo() {
        //given
        GitHub gitHub = getGitHub();
        LocalDate createdAt = LocalDate.of(2024, 2, 5);

        //when
        List<GHPullRequest> pullRequest = githubService.getPullRequestByDate(gitHub, repository, createdAt);

        //then
        assertThat(pullRequest.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("특정 레포지토리에 연결이 되지 않으면 예외를 발생한다.")
    public void should_throwException_when_repoConnectionInvalid() {
        //given
        GitHub gitHub = getGitHub();
        String repositoryName = "Fake";
        LocalDate createdAt = LocalDate.of(2024, 2, 5);

        //when & then
        assertThatThrownBy(() -> githubService.getPullRequestByDate(gitHub, repositoryName, createdAt))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_REPOSITORY_INCORRECT.getMessage());
    }

    @Test
    @DisplayName("사용자가 가지고 있는 레포지토리 리스트들을 반환할 수 있다.")
    public void should_returnRepositories() {
        //given
        GitHub gitHub = getGitHub();

        //when
        List<GHRepository> repositoryList = githubService.getRepositoryList(gitHub);

        //then
        assertThat(repositoryList.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Pr 인증을 시도 했을 때, KST 기준으로 생성된 PR 리스트를 불러올 수 있다.")
    public void should_searchPR_when_tryToCertificate() {
        //given
        GitHub gitHub = getGitHub();
        LocalDate kstDate = LocalDate.of(2024, 2, 25);

        //when
        List<GHPullRequest> pullRequests = githubService.getPullRequestByDate(gitHub, repository, kstDate);

        //then
        assertThat(pullRequests.size()).isEqualTo(2);
    }

    private GitHub getGitHub() {
        return githubService.getGithubConnection(personalKey).join();
    }
}