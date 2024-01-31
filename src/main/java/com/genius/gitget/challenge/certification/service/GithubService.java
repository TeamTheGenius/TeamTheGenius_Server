package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_CONNECTION_FAILED;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_ID_INCORRECT;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_REPOSITORY_INCORRECT;

import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GithubService {

    public GitHub getGithubConnection(String personalKey) {
        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(personalKey).build();
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
            GHRepository repository = gitHub.getRepository(repositoryName);
            validateRepositoryName(repository.getFullName(), repositoryName);
        } catch (IllegalArgumentException | IOException e) {
            throw new BusinessException(e);
        }
    }

    private void validateRepositoryName(String repoFullName, String repoName) {
        if (!repoFullName.equals(repoName)) {
            throw new BusinessException(GITHUB_REPOSITORY_INCORRECT);
        }
    }
}
