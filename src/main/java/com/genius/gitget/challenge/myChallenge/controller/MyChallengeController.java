package com.genius.gitget.challenge.myChallenge.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.myChallenge.facade.MyChallengeFacade;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.annotation.GitGetUser;
import com.genius.gitget.global.util.response.dto.ListResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final MyChallengeFacade myChallengeFacade;

    @GetMapping("/my/pre-activity")
    public ResponseEntity<ListResponse<PreActivityResponse>> getPreActivityChallenges(
            @GitGetUser User user
    ) {
        List<PreActivityResponse> preActivityInstances = myChallengeFacade.getPreActivityInstances(
                user, DateUtil.convertToKST(LocalDateTime.now()));

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), preActivityInstances)
        );
    }


    @GetMapping("/my/activity")
    public ResponseEntity<ListResponse<ActivatedResponse>> getActivatedChallenges(
            @GitGetUser User user
    ) {
        List<ActivatedResponse> activatedInstances = myChallengeFacade.getActivatedInstances(
                user, DateUtil.convertToKST(LocalDateTime.now()));

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), activatedInstances)
        );
    }

    @GetMapping("/my/done")
    public ResponseEntity<ListResponse<DoneResponse>> getDoneChallenges(
            @GitGetUser User user
    ) {
        List<DoneResponse> doneInstances = myChallengeFacade.getDoneInstances(
                user, DateUtil.convertToKST(LocalDateTime.now()));

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), doneInstances)
        );
    }

    @GetMapping("/reward/{instanceId}")
    public ResponseEntity<SingleResponse<DoneResponse>> getRewards(
            @GitGetUser User user,
            @PathVariable Long instanceId
    ) {
        LocalDate kstDate = DateUtil.convertToKST(LocalDateTime.now());
        RewardRequest rewardRequest = new RewardRequest(user.getId(), instanceId, kstDate);
        DoneResponse doneResponse = myChallengeFacade.getRewards(rewardRequest);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), doneResponse)
        );
    }
}
