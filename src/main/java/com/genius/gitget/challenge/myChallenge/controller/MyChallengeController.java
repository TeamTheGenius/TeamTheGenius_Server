package com.genius.gitget.challenge.myChallenge.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.myChallenge.facade.MyChallengeFacadeService;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.response.dto.ListResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
@CrossOrigin
public class MyChallengeController {
    private final MyChallengeFacadeService myChallengeFacadeService;


    @GetMapping("/my/pre-activity")
    public ResponseEntity<ListResponse<PreActivityResponse>> getPreActivityChallenges(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<PreActivityResponse> preActivityInstances = myChallengeFacadeService.getPreActivityInstances(
                userPrincipal.getUser(),
                DateUtil.convertToKST(LocalDateTime.now()));

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), preActivityInstances)
        );
    }


    @GetMapping("/my/activity")
    public ResponseEntity<ListResponse<ActivatedResponse>> getActivatedChallenges(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<ActivatedResponse> activatedInstances = myChallengeFacadeService.getActivatedInstances(
                userPrincipal.getUser(),
                DateUtil.convertToKST(LocalDateTime.now()));

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), activatedInstances)
        );
    }

    @GetMapping("/my/done")
    public ResponseEntity<ListResponse<DoneResponse>> getDoneChallenges(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<DoneResponse> doneInstances = myChallengeFacadeService.getDoneInstances(
                userPrincipal.getUser(),
                DateUtil.convertToKST(LocalDateTime.now()));

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), doneInstances)
        );
    }

    @GetMapping("/reward/{instanceId}")
    public ResponseEntity<SingleResponse<DoneResponse>> getRewards(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long instanceId
    ) {
        LocalDate kstDate = DateUtil.convertToKST(LocalDateTime.now());
        RewardRequest rewardRequest = new RewardRequest(userPrincipal.getUser(), instanceId, kstDate);
        DoneResponse doneResponse = myChallengeFacadeService.getRewards(rewardRequest, false);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), doneResponse)
        );
    }
}
