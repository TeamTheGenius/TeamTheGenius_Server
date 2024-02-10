package com.genius.gitget.challenge.certification.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.dto.GithubTokenRequest;
import com.genius.gitget.challenge.certification.dto.RepositoryRequest;
import com.genius.gitget.challenge.certification.service.GithubService;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.ListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/register/repository")
    public ResponseEntity<ListResponse<String>> getPublicRepositories(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<String> repositories = githubService.getPublicRepositories(userPrincipal.getUser());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), repositories)
        );
    }

    @PostMapping("/register/repository")
    public ResponseEntity<CommonResponse> registerRepository(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody RepositoryRequest repositoryRequest
    ) {

        githubService.registerRepository(userPrincipal.getUser(), repositoryRequest.instanceId(),
                repositoryRequest.repositoryName());

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }
}
