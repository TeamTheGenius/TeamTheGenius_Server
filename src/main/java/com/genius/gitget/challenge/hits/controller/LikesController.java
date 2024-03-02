package com.genius.gitget.challenge.hits.controller;

import com.genius.gitget.challenge.hits.dto.UserLikesAddRequest;
import com.genius.gitget.challenge.hits.dto.UserLikesResponse;
import com.genius.gitget.challenge.hits.service.LikesService;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SlicingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/profile")
public class LikesController {
    private final LikesService likesService;

    // 좋아요 목록 조회
    @GetMapping("/likes")
    public ResponseEntity<SlicingResponse<UserLikesResponse>> getLikesListOfUser(Pageable pageable,
                                                                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        likesService.getLikesList(userPrincipal.getUser(), pageRequest);

        return null;
    }

    // 좋아요 목록 추가
    @PostMapping("/likes")
    public ResponseEntity<CommonResponse> addLikes(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @RequestBody UserLikesAddRequest userLikesAddRequest) {
        likesService.addLikes(userPrincipal.getUser(), userLikesAddRequest.getIdentifier(),
                userLikesAddRequest.getInstanceId());
        return ResponseEntity.ok().body(
                new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage())
        );
    }

    // 좋아요 목록 삭제
    @DeleteMapping("/likes/{likesId}")
    public ResponseEntity<CommonResponse> deleteLikes(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                      @PathVariable(value = "likesId") Long likesId) {
        likesService.deleteLikes(userPrincipal.getUser(), likesId);
        return ResponseEntity.ok().body(
                new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.CREATED.getMessage())
        );
    }
}
