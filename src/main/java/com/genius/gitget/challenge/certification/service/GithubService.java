package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_PR_NOT_FOUND;

import com.genius.gitget.challenge.certification.dto.PullRequestResponse;
import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
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

        String encryptedToken = encryptUtil.encrypt(githubToken);
        user.updateGithubPersonalToken(encryptedToken);
        userService.save(user);
    }

    @Transactional
    public void verifyRepository(User user, String repository) {
        GitHub gitHub = githubProvider.getGithubConnection(user);

        String repositoryFullName = githubProvider.getRepoFullName(gitHub, repository);
        githubProvider.validateGithubRepository(gitHub, repositoryFullName);
    }

    public List<String> getPublicRepositories(User user) {
        GitHub gitHub = githubProvider.getGithubConnection(user);
        List<GHRepository> repositoryList = githubProvider.getRepositoryList(gitHub);
        return repositoryList.stream()
                .map(GHRepository::getName)
                .toList();
    }

    //TODO: PR이 날라온 브랜치의 이름이 정해진 규칙에 맞는지 여부 확인 필요
    public List<PullRequestResponse> verifyPullRequest(User user, String repositoryName, LocalDate targetDate) {
        List<PullRequestResponse> responses = getPullRequestListByDate(user, repositoryName, targetDate);

        if (responses.isEmpty()) {
            throw new BusinessException(GITHUB_PR_NOT_FOUND);
        }
        return responses;
    }

    public List<PullRequestResponse> getPullRequestListByDate(User user, String repositoryName,
                                                              LocalDate targetDate) {
        GitHub gitHub = githubProvider.getGithubConnection(user);

        List<GHPullRequest> pullRequest = githubProvider.getPullRequestByDate(gitHub, repositoryName, targetDate);

        return pullRequest.stream()
                .map(PullRequestResponse::create)
                .toList();
    }
}
