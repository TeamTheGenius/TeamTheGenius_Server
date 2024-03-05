package com.genius.gitget.profile.controller;

import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import com.genius.gitget.profile.dto.UserChallengeResultResponse;
import com.genius.gitget.profile.dto.UserInformationResponse;
import com.genius.gitget.profile.dto.UserInformationUpdateRequest;
import com.genius.gitget.profile.dto.UserTagsUpdateRequest;
import com.genius.gitget.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    // TODO 마이페이지 - 결제 내역 조회

    // 마이페이지 - 사용자 정보
    @GetMapping
    public ResponseEntity<SingleResponse<UserInformationResponse>> getUserInformation(@AuthenticationPrincipal
                                                                                      UserPrincipal userPrincipal) {
        UserInformationResponse userInformation = profileService.getUserInformation(userPrincipal.getUser());
        return ResponseEntity.ok()
                .body(new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),
                        userInformation)
                );
    }

    // 마이페이지 - 회원 정보 수정
    @PostMapping("/information")
    public ResponseEntity<CommonResponse> updateUserInformation(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                @RequestPart(value = "data") UserInformationUpdateRequest userInformationUpdateRequest,
                                                                @RequestPart(value = "files", required = false) MultipartFile multipartFile,
                                                                @RequestPart(value = "type") String type) {
        profileService.updateUserInformation(userPrincipal.getUser(), userInformationUpdateRequest, multipartFile,
                type);

        return ResponseEntity.ok()
                .body(new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    // 마이페이지 - 관심사 수정
    @PostMapping("/interest")
    public ResponseEntity<CommonResponse> updateUserTags(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                         @RequestBody UserTagsUpdateRequest userTagsUpdateRequest) {
        profileService.updateUserTags(userPrincipal.getUser(), userTagsUpdateRequest);

        return ResponseEntity.ok()
                .body(new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    // 마이페이지 - 챌린지 현황
    @GetMapping("/challenges")
    public ResponseEntity<SingleResponse<UserChallengeResultResponse>> getUserChallengeResult(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserChallengeResultResponse userChallengeResult = profileService.getUserChallengeResult(
                userPrincipal.getUser());
        return ResponseEntity.ok()
                .body(new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),
                        userChallengeResult));
    }

    // 마이페이지 - 탈퇴하기
    @DeleteMapping
    public ResponseEntity<CommonResponse> deleteUserInformation(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        profileService.deleteUserInformation(userPrincipal.getUser());

        return ResponseEntity.ok()
                .body(new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }
}
