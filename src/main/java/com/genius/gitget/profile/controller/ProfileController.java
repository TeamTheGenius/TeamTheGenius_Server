package com.genius.gitget.profile.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import com.genius.gitget.profile.dto.UserChallengeResultResponse;
import com.genius.gitget.profile.dto.UserDetailsInformationResponse;
import com.genius.gitget.profile.dto.UserIndexResponse;
import com.genius.gitget.profile.dto.UserInformationRequest;
import com.genius.gitget.profile.dto.UserInformationResponse;
import com.genius.gitget.profile.dto.UserInformationUpdateRequest;
import com.genius.gitget.profile.dto.UserInterestResponse;
import com.genius.gitget.profile.dto.UserInterestUpdateRequest;
import com.genius.gitget.profile.dto.UserPointResponse;
import com.genius.gitget.profile.dto.UserSignoutRequest;
import com.genius.gitget.profile.service.ProfileFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileFacade profileFacade;

    // 마이페이지 - 사용자 상세 정보 조회
    @GetMapping
    public ResponseEntity<SingleResponse<UserDetailsInformationResponse>> getUserDetailsInformation(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserDetailsInformationResponse userInformation = profileFacade.getUserDetailsInformation(
                userPrincipal.getUser());
        return ResponseEntity.ok()
                .body(new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(),
                        userInformation)
                );
    }

    // 사용자 정보 조회
    @PostMapping
    public ResponseEntity<SingleResponse<UserInformationResponse>> getUserInformation(
            @RequestBody UserInformationRequest userInformationRequest) {
        UserInformationResponse userInformation = profileFacade.getUserInformation(userInformationRequest.getUserId());
        return ResponseEntity.ok()
                .body(new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(),
                        userInformation)
                );
    }

    // 마이페이지 - 회원 정보 수정
    @PostMapping("/information")
    public ResponseEntity<SingleResponse<UserIndexResponse>> updateUserInformation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserInformationUpdateRequest userInformationUpdateRequest) {

        Long userId = profileFacade.updateUserInformation(userPrincipal.getUser(), userInformationUpdateRequest);
        UserIndexResponse userIndexResponse = new UserIndexResponse(userId);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), userIndexResponse)
        );
    }

    // 마이페이지 - 관심사 조회
    @GetMapping("/interest")
    public ResponseEntity<SingleResponse<UserInterestResponse>> getUserInterest(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserInterestResponse userInterest = profileFacade.getUserInterest(userPrincipal.getUser());

        return ResponseEntity.ok()
                .body(new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(),
                        userInterest));
    }


    // 마이페이지 - 관심사 수정
    @PostMapping("/interest")
    public ResponseEntity<CommonResponse> updateUserTags(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                         @RequestBody UserInterestUpdateRequest userInterestUpdateRequest) {
        profileFacade.updateUserTags(userPrincipal.getUser(), userInterestUpdateRequest);

        return ResponseEntity.ok()
                .body(new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage()));
    }


    // 마이페이지 - 챌린지 현황
    @GetMapping("/challenges")
    public ResponseEntity<SingleResponse<UserChallengeResultResponse>> getUserChallengeResult(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserChallengeResultResponse userChallengeResult = profileFacade.getUserChallengeResult(
                userPrincipal.getUser());
        return ResponseEntity.ok()
                .body(new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(),
                        userChallengeResult));
    }


    // 마이페이지 - 탈퇴하기
    @DeleteMapping
    public ResponseEntity<CommonResponse> deleteUserInformation(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                @RequestBody UserSignoutRequest userSignoutRequest) {
        profileFacade.deleteUserInformation(userPrincipal.getUser(), userSignoutRequest.getReason());

        return ResponseEntity.ok()
                .body(new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage()));
    }


    // 포인트 조회
    @GetMapping("/point")
    public ResponseEntity<SingleResponse<UserPointResponse>> getUserPoint(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserPointResponse userPoint = profileFacade.getUserPoint(userPrincipal.getUser());

        return ResponseEntity.ok()
                .body(new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(),
                        userPoint));
    }
}
