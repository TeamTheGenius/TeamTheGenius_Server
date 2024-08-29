package com.genius.gitget.challenge.certification.facade;

import com.genius.gitget.challenge.certification.dto.github.PullRequestResponse;
import com.genius.gitget.challenge.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GithubFacade {
    CompletableFuture<Void> registerGithubPersonalToken(User user, String githubToken);

    CompletableFuture<Void> verifyGithubToken(User user);

    CompletableFuture<Void> verifyRepository(User user, String repository);

    List<String> getPublicRepositories(User user);

    List<PullRequestResponse> verifyPullRequest(User user, String repositoryName, LocalDate targetDate);

    List<PullRequestResponse> getPullRequestListByDate(User user, String repositoryName,LocalDate targetDate);

}
