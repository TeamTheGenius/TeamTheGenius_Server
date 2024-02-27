package com.genius.gitget.challenge.myChallenge.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.myChallenge.service.MyChallengeService;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.response.dto.ListResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/challenges/my")
@RequiredArgsConstructor
public class MyChallengeController {
    private final MyChallengeService myChallengeService;


    @GetMapping("/pre-activity")
    public ResponseEntity<ListResponse<PreActivityResponse>> getPreActivityChallenges(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<PreActivityResponse> preActivityInstances = myChallengeService.getPreActivityInstances(
                userPrincipal.getUser(),
                LocalDate.now());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), preActivityInstances)
        );
    }


    @GetMapping("/activity")
    public ResponseEntity<ListResponse<ActivatedResponse>> getActivatedChallenges(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<ActivatedResponse> activatedInstances = myChallengeService.getActivatedInstances(
                userPrincipal.getUser(),
                LocalDate.now());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), activatedInstances)
        );
    }

    @GetMapping("/done")
    public ResponseEntity<ListResponse<DoneResponse>> getDoneChallenges(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<DoneResponse> doneInstances = myChallengeService.getDoneInstances(
                userPrincipal.getUser(),
                LocalDate.now());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), doneInstances)
        );
    }
}
