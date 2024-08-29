package com.genius.gitget.challenge.certification.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.dto.github.GithubTokenRequest;
import com.genius.gitget.challenge.certification.dto.github.PullRequestResponse;
import com.genius.gitget.challenge.certification.facade.GithubFacade;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.annotation.GitGetUser;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.ListResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certification")
public class GithubController {
	private final GithubFacade githubFacade;

	@PostMapping("/register/token")
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse registerGithubToken(@GitGetUser User user, @RequestBody GithubTokenRequest githubTokenRequest) {
		githubFacade.registerGithubPersonalToken(user, githubTokenRequest.githubToken());

		return new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage());
	}

	@GetMapping("/repositories")
	@ResponseStatus(HttpStatus.OK)
	public ListResponse<String> getPublicRepositories(@GitGetUser User user) {
		List<String> publicRepositories = githubFacade.getPublicRepositories(user);
		return new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), publicRepositories);
	}

	@GetMapping("/verify/token")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse verifyGithubToken(@GitGetUser User user) {
		githubFacade.verifyGithubToken(user);

		return new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage());
	}

	@GetMapping("/verify/repository")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse verifyRepository(@GitGetUser User user, @RequestParam String repo) {
		githubFacade.verifyRepository(user, repo);

		return new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage());
	}

	@GetMapping("/verify/pull-request")
	@ResponseStatus(HttpStatus.OK)
	public ListResponse<PullRequestResponse> verifyPullRequest(@GitGetUser User user, @RequestParam String repo) {
		List<PullRequestResponse> listCompletableFuture = githubFacade.verifyPullRequest(user, repo, DateUtil.convertToKST(LocalDateTime.now()));
		return new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), listCompletableFuture);
	}
}
