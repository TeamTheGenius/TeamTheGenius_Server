package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_CONNECTION_FAILED;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_ID_INCORRECT;

import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GithubService {

    public void validateGithubConnection(String githubId, String personalKey) {
        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(personalKey).build();
            gitHub.checkApiUrlValidity();

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
}
