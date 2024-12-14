package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_PR_NOT_FOUND;

import com.genius.gitget.challenge.certification.dto.github.PullRequestResponse;
import com.genius.gitget.challenge.certification.facade.GithubFacade;
import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GithubFacadeService implements GithubFacade {
    private final UserService userService;
    private final GithubService githubService;
    private final EncryptUtil encryptUtil;

    @Override
    @Transactional
    public CompletableFuture<Void> registerGithubPersonalToken(User user, String githubToken) {
        return githubService.getGithubConnection(githubToken)
                .thenCompose(github -> CompletableFuture.runAsync(() -> {
                    githubService.validateGithubConnection(github, user.getIdentifier());
                    String encryptedToken = encryptUtil.encrypt(githubToken);
                    user.updateGithubPersonalToken(encryptedToken);
                    userService.save(user);
                }));
    }

    @Override
    public CompletableFuture<Void> verifyGithubToken(User user) {
        String githubToken = encryptUtil.decrypt(user.getGithubToken());

        return githubService.getGithubConnection(githubToken)
                .thenCompose(github -> CompletableFuture.runAsync(() -> {
                    githubService.validateGithubConnection(github, user.getIdentifier());
                }));
    }

    @Override
    @Transactional
    public CompletableFuture<Void> verifyRepository(User user, String repository) {
        return githubService.getGithubConnection(user)
                .thenCompose(github -> CompletableFuture.runAsync(() -> {
                    String repositoryFullName = githubService.getRepoFullName(github, repository);
                    githubService.validateGithubRepository(github, repositoryFullName);
                }));
    }

    @Override
    public List<String> getPublicRepositories(User user) {
        GitHub gitHub = githubService.getGithubConnection(user).join();
        List<GHRepository> repositoryList = githubService.getRepositoryList(gitHub);
        return repositoryList.stream()
                .map(GHRepository::getName)
                .toList();
    }

    @Override
    public List<PullRequestResponse> verifyPullRequest(User user, String repositoryName, LocalDate targetDate) {
        List<PullRequestResponse> responses = getPullRequestListByDate(user, repositoryName, targetDate);

        if (responses.isEmpty()) {
            throw new BusinessException(GITHUB_PR_NOT_FOUND);
        }
        return responses;
    }

    @Override
    public List<PullRequestResponse> getPullRequestListByDate(User user, String repositoryName, LocalDate targetDate) {
        GitHub gitHub = githubService.getGithubConnection(user).join();

        List<GHPullRequest> pullRequest = githubService.getPullRequestByDate(gitHub, repositoryName, targetDate);

        return pullRequest.stream()
                .map(PullRequestResponse::create)
                .toList();
    }
}
