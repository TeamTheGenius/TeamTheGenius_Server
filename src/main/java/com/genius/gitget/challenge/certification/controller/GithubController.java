package com.genius.gitget.challenge.certification.controller;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_PR_NOT_FOUND;
import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.dto.GithubTokenRequest;
import com.genius.gitget.challenge.certification.dto.PullRequestResponse;
import com.genius.gitget.challenge.certification.service.GithubService;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.ListResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certification")
public class GithubController {
    private final GithubService githubService;

    @PostMapping("/register/token")
    public ResponseEntity<CommonResponse> registerGithubToken(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody GithubTokenRequest githubTokenRequest
    ) {
        githubService.registerGithubPersonalToken(userPrincipal.getUser(), githubTokenRequest.githubToken());

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }

    @GetMapping("/repositories")
    public ResponseEntity<ListResponse<String>> getPublicRepositories(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<String> repositories = githubService.getPublicRepositories(userPrincipal.getUser());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), repositories)
        );
    }

    @GetMapping("/verify/repository")
    public ResponseEntity<CommonResponse> registerRepository(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String repo
    ) {

        githubService.verifyRepository(userPrincipal.getUser(), repo);

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }

    @GetMapping("/verify/pull-request")
    public ResponseEntity<ListResponse<PullRequestResponse>> verify(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String repo
    ) {

        List<PullRequestResponse> pullRequestResponses = githubService.getPullRequestListByDate(
                userPrincipal.getUser(), repo, LocalDate.now());

        if (pullRequestResponses.isEmpty()) {
            throw new BusinessException(GITHUB_PR_NOT_FOUND);
        }

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), pullRequestResponses)
        );
    }
}
