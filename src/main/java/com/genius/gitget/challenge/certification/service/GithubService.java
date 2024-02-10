package com.genius.gitget.challenge.certification.service;

import com.genius.gitget.challenge.certification.dto.PullRequestResponse;
import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import java.time.LocalDate;
import java.util.List;
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
public class GithubService {
    private final UserService userService;
    private final GithubProvider githubProvider;
    private final EncryptUtil encryptUtil;

    @Transactional
    public void registerGithubPersonalToken(User user, String githubToken) {
        GitHub gitHub = githubProvider.getGithubConnection(githubToken);
        githubProvider.validateGithubConnection(gitHub, user.getIdentifier());

        String encryptedToken = encryptUtil.encryptPersonalToken(githubToken);
        user.updateGithubPersonalToken(encryptedToken);
        userService.save(user);
    }

    @Transactional
    public void verifyRepository(User user, String repository) {
        GitHub gitHub = githubProvider.getGithubConnection(user);

        String repositoryFullName = user.getIdentifier() + "/" + repository;
        githubProvider.validateGithubRepository(gitHub, repositoryFullName);
    }

    public List<String> getPublicRepositories(User user) {
        GitHub gitHub = githubProvider.getGithubConnection(user);
        List<GHRepository> repositoryList = githubProvider.getRepositoryList(gitHub);
        return repositoryList.stream()
                .map(String::valueOf)
                .toList();
    }

    public List<PullRequestResponse> getPullRequestListByDate(User user, String repositoryName, LocalDate targetDate) {
        GitHub gitHub = githubProvider.getGithubConnection(user);

        List<GHPullRequest> pullRequest = githubProvider.getPullRequestByDate(gitHub, repositoryName, targetDate)
                .nextPage();

        return pullRequest.stream()
                .map(PullRequestResponse::create)
                .toList();
    }
}
