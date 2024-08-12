package com.genius.gitget.challenge.instance.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.JOIN_SUCCESS;
import static com.genius.gitget.global.util.exception.SuccessCode.QUIT_SUCCESS;
import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.dto.detail.InstanceResponse;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.dto.detail.JoinResponse;
import com.genius.gitget.challenge.instance.service.InstanceDetailFacade;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
public class InstanceDetailController {
    private final InstanceDetailFacade instanceDetailFacade;


    @GetMapping("/{instanceId}")
    public ResponseEntity<SingleResponse<InstanceResponse>> getInstanceDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long instanceId
    ) {
        InstanceResponse instanceDetailInformation = instanceDetailFacade.getInstanceDetailInformation(
                userPrincipal.getUser(), instanceId);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), instanceDetailInformation)
        );
    }

    @PostMapping("/{instanceId}")
    public ResponseEntity<SingleResponse<JoinResponse>> joinChallenge(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long instanceId,
            @RequestParam String repo
    ) {
        LocalDate kstDate = DateUtil.convertToKST(LocalDateTime.now());
        JoinRequest joinRequest = JoinRequest.builder()
                .instanceId(instanceId)
                .repository(repo)
                .todayDate(kstDate)
                .build();
        JoinResponse joinResponse = instanceDetailFacade.joinNewChallenge(userPrincipal.getUser(), joinRequest);

        return ResponseEntity.ok().body(
                new SingleResponse<>(JOIN_SUCCESS.getStatus(), JOIN_SUCCESS.getMessage(), joinResponse)
        );
    }

    @DeleteMapping("/{instanceId}")
    public ResponseEntity<SingleResponse<JoinResponse>> quitChallenge(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long instanceId
    ) {
        JoinResponse joinResponse = instanceDetailFacade.quitChallenge(userPrincipal.getUser(), instanceId);

        return ResponseEntity.ok().body(
                new SingleResponse<>(QUIT_SUCCESS.getStatus(), QUIT_SUCCESS.getMessage(), joinResponse)
        );
    }
}
