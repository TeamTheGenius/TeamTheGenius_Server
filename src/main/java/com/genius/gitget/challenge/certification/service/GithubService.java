package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_CONNECTION_FAILED;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_ID_INCORRECT;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_REPOSITORY_INCORRECT;

import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHDirection;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestSearchBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GHRepositorySearchBuilder.Sort;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GithubService {
    private final UserService userService;


    @Async("threadExecutor")
    public CompletableFuture<GitHub> getGithubConnection(String githubToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                GitHub gitHub = new GitHubBuilder().withOAuthToken(githubToken).build();
                gitHub.checkApiUrlValidity();
                return gitHub;
            } catch (IOException e) {
                throw new CompletionException(new BusinessException(GITHUB_CONNECTION_FAILED));
            }
        });
    }

    @Async("threadExecutor")
    public CompletableFuture<GitHub> getGithubConnection(User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String githubToken = userService.getGithubToken(user);
                GitHub gitHub = new GitHubBuilder().withOAuthToken(githubToken).build();
                gitHub.checkApiUrlValidity();
                return gitHub;
            } catch (IOException e) {
                throw new BusinessException(GITHUB_CONNECTION_FAILED);
            }
        });
    }

    public void validateGithubConnection(GitHub gitHub, String githubId) {
        try {
            String accountId = gitHub.getMyself().getLogin();
            validateGithubAccount(githubId, accountId);
        } catch (IOException e) {
            throw new BusinessException(GITHUB_CONNECTION_FAILED);
        }
    }

    private void validateGithubAccount(String githubId, String accountId) {
        if (!githubId.equals(accountId)) {
            throw new BusinessException(GITHUB_ID_INCORRECT);
        }
    }

    public void validateGithubRepository(GitHub gitHub, String repositoryFullName) {
        try {
            gitHub.getRepository(repositoryFullName);
        } catch (GHFileNotFoundException e) {
            throw new BusinessException(GITHUB_REPOSITORY_INCORRECT);
        } catch (IllegalArgumentException | IOException e) {
            throw new BusinessException(e);
        }
    }

    public List<GHRepository> getRepositoryList(GitHub gitHub) {
        try {
            GHRepositorySearchBuilder builder = gitHub.searchRepositories()
                    .user(getGHUser(gitHub).getLogin())
                    .sort(Sort.UPDATED)
                    .order(GHDirection.DESC);
            return builder.list().iterator().nextPage();
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    public List<GHPullRequest> getPullRequestByDate(GitHub gitHub, String repositoryName,
                                                    LocalDate kstDate) {
        try {
            GHRepository repository = gitHub.getRepository(getRepoFullName(gitHub, repositoryName));
            GHPullRequestSearchBuilder prSearchBuilder = gitHub.searchPullRequests()
                    .repo(repository)
                    .author(getGHUser(gitHub))
                    .created(kstDate.minusDays(1), kstDate);

            return prSearchBuilder.list().iterator().nextPage().stream()
                    .filter(pr -> isEqualToKST(pr, kstDate))
                    .toList();

        } catch (GHFileNotFoundException e) {
            throw new BusinessException(GITHUB_REPOSITORY_INCORRECT);
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    private boolean isEqualToKST(GHPullRequest ghPullRequest, LocalDate targetDate) {
        try {
            LocalDate kst = DateUtil.convertToKST(ghPullRequest.getCreatedAt());
            return kst.isEqual(targetDate);
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    public List<String> filterValidPR(List<GHPullRequest> ghPullRequests, String prTemplate) {
        return ghPullRequests.stream()
                .filter(ghPullRequest -> {
                    if (ghPullRequest.getBody() == null) {
                        return false;
                    }
                    return ghPullRequest.getBody().contains(prTemplate);
                })
                .map(ghPullRequest -> ghPullRequest.getHtmlUrl().toString())
                .toList();
    }

    private GHUser getGHUser(GitHub gitHub) throws IOException {
        String accountId = gitHub.getMyself().getLogin();
        return gitHub.getUser(accountId);
    }

    public String getRepoFullName(GitHub gitHub, String repositoryName) {
        try {
            return gitHub.getMyself().getLogin() + "/" + repositoryName;
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
}
