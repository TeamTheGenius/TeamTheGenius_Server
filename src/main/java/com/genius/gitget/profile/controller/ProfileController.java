package com.genius.gitget.profile.controller;

import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import com.genius.gitget.profile.dto.UserChallengeResultResponse;
import com.genius.gitget.profile.dto.UserInformationResponse;
import com.genius.gitget.profile.dto.UserInformationUpdateRequest;
import com.genius.gitget.profile.dto.UserPaymentDetailsResponse;
import com.genius.gitget.profile.dto.UserTagsUpdateRequest;
import com.genius.gitget.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    // 마이페이지 - 사용자 정보 : identifier, nickname, image, description, point
    // TODO 챌린지 성공률 (?)
    @GetMapping("/{identifier}")
    public ResponseEntity<SingleResponse<UserInformationResponse>> getUserInformation(
            @PathVariable(value = "identifier") String identifier) {
        UserInformationResponse userInformation = profileService.getUserInformation(identifier);
        return ResponseEntity.ok()
                .body(new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),
                        userInformation)
                );
    }

    // 마이페이지 - 회원 정보 수정 : nickname, image, description
    @PostMapping("/information")
    public ResponseEntity<CommonResponse> updateUserInformation(
            @RequestPart(value = "data") UserInformationUpdateRequest userInformationUpdateRequest,
            @RequestPart(value = "files", required = false) MultipartFile multipartFile) {
        profileService.updateUserInformation(userInformationUpdateRequest, multipartFile);

        return ResponseEntity.ok()
                .body(new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    // 마이페이지 - 관심사 수정 : tags
    @PostMapping("/tags")
    public ResponseEntity<CommonResponse> updateUserTags(UserTagsUpdateRequest userTagsUpdateRequest) {
        profileService.updateUserTags(userTagsUpdateRequest);

        return ResponseEntity.ok()
                .body(new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    // 마이페이지 - 챌린지 현황 : 시작 전, 진행 중, 완료/실패
    @GetMapping("/challenges/{identifier}")
    public ResponseEntity<SingleResponse<UserChallengeResultResponse>> getUserChallengeResult(
            @PathVariable(value = "identifier") String identifier) {
        UserChallengeResultResponse userChallengeResult = profileService.getUserChallengeResult(identifier);
        return ResponseEntity.ok()
                .body(new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),
                        userChallengeResult));
    }

    // 마이페이지 - 탈퇴하기 : 탈퇴 제목, 탈퇴 사유
    @DeleteMapping("/{identifier}")
    public ResponseEntity<CommonResponse> deleteUserInformation(@PathVariable(value = "identifier") String identifier) {
        profileService.deleteUserInformation(identifier);

        return ResponseEntity.ok()
                .body(new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    // TODO 마이페이지 - 결제 내역 조회
    @GetMapping("/payment/{identifier}")
    public ResponseEntity<SingleResponse<UserPaymentDetailsResponse>> getUserPayment(
            @PathVariable(value = "identifier") String identifier) {
        profileService.getUserPayment(identifier);

    }
}
