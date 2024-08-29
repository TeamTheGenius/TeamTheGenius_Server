package com.genius.gitget.challenge.certification.service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.gitget.challenge.certification.dto.github.PullRequestResponse;
import com.genius.gitget.challenge.certification.facade.GithubFacade;
import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
			.thenCompose(gitHub -> CompletableFuture.runAsync(() -> {
				try {
					githubService.validateGithubConnection(gitHub, user.getIdentifier());
					String encryptedToken = encryptUtil.encrypt(githubToken);
					user.updateGithubPersonalToken(encryptedToken);
					userService.save(user);
				} catch (Exception e) {
					throw new BusinessException("Failed to register GitHub token", e);
				}
			}));
	}

	@Override
	public CompletableFuture<Void> verifyGithubToken(User user) {
		String githubToken = encryptUtil.decrypt(user.getGithubToken());
		return githubService.getGithubConnection(githubToken)
			.thenAccept(gitHub -> githubService.validateGithubConnection(gitHub, user.getIdentifier()));
	}

	@Override
	@Transactional
	public CompletableFuture<Void> verifyRepository(User user, String repository) {
		return githubService.getGithubConnection(user)
			.thenCompose(gitHub -> CompletableFuture.runAsync(() -> {
				String repositoryFullName = githubService.getRepoFullName(gitHub, repository);
				githubService.validateGithubRepository(gitHub, repositoryFullName);
			}));
	}

	@Override
	public List<String> getPublicRepositories(User user) {
		CompletableFuture<GitHub> githubConnection = githubService.getGithubConnection(user);
		GitHub gitHub = githubConnection.join();
		List<GHRepository> repositoryList = githubService.getRepositoryList(gitHub);
		return repositoryList.stream()
			.map(GHRepository::getName)
			.toList();
	}

	@Override
	public List<PullRequestResponse> verifyPullRequest(User user, String repositoryName, LocalDate targetDate) {
		return getPullRequestListByDate(user, repositoryName, targetDate);
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
