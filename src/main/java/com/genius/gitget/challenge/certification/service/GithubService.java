package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_CONNECTION_FAILED;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_ID_INCORRECT;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_REPOSITORY_INCORRECT;

import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestSearchBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class GithubService {
    private final int PAGE_SIZE = 10;

    public GitHub getGithubConnection(String githubToken) {
        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(githubToken).build();
            gitHub.checkApiUrlValidity();
            return gitHub;
        } catch (IOException e) {
            throw new BusinessException(GITHUB_CONNECTION_FAILED);
        }
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

    public void validateGithubRepository(GitHub gitHub, String repositoryName) {
        try {
            gitHub.getRepository(repositoryName);
        } catch (GHFileNotFoundException e) {
            throw new BusinessException(GITHUB_REPOSITORY_INCORRECT);
        } catch (IllegalArgumentException | IOException e) {
            throw new BusinessException(e);
        }
    }

    public List<GHPullRequest> getPullRequestByDate(GitHub gitHub, String repositoryName, LocalDate createdAt) {
        try {
            GHRepository repository = gitHub.getRepository(repositoryName);
            GHPullRequestSearchBuilder builder = gitHub.searchPullRequests()
                    .repo(repository)
                    .author(getGHUser(gitHub))
                    .created(createdAt);

            return builder.list()._iterator(PAGE_SIZE).nextPage();

        } catch (GHFileNotFoundException e) {
            throw new BusinessException(GITHUB_REPOSITORY_INCORRECT);
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    private GHUser getGHUser(GitHub gitHub) throws IOException {
        String accountId = gitHub.getMyself().getLogin();
        return gitHub.getUser(accountId);
    }
}
