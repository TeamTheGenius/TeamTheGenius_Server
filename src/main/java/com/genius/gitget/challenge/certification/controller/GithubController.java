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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final GithubFacade githubFacade;

    @PostMapping("/register/token")
    public ResponseEntity<CommonResponse> registerGithubToken(
            @GitGetUser User user,
            @RequestBody GithubTokenRequest githubTokenRequest
    ) {
        githubFacade.registerGithubPersonalToken(user, githubTokenRequest.githubToken());

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }

    @GetMapping("/repositories")
    public ResponseEntity<ListResponse<String>> getPublicRepositories(
            @GitGetUser User user
    ) {
        List<String> repositories = githubFacade.getPublicRepositories(user);

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), repositories)
        );
    }

    @GetMapping("/verify/token")
    public ResponseEntity<CommonResponse> verifyGithubToken(
            @GitGetUser User user
    ) {
        githubFacade.verifyGithubToken(user);

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }

    @GetMapping("/verify/repository")
    public ResponseEntity<CommonResponse> verifyRepository(
            @GitGetUser User user,
            @RequestParam String repo
    ) {

        githubFacade.verifyRepository(user, repo);

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }

    @GetMapping("/verify/pull-request")
    public ResponseEntity<ListResponse<PullRequestResponse>> verifyPullRequest(
            @GitGetUser User user,
            @RequestParam String repo
    ) {

        List<PullRequestResponse> pullRequestResponses = githubFacade.verifyPullRequest(
                user, repo, DateUtil.convertToKST(LocalDateTime.now())
        );

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), pullRequestResponses)
        );
    }
}
