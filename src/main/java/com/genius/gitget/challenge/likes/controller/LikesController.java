package com.genius.gitget.challenge.likes.controller;

import com.genius.gitget.challenge.likes.dto.UserLikesAddRequest;
import com.genius.gitget.challenge.likes.dto.UserLikesAddResponse;
import com.genius.gitget.challenge.likes.dto.UserLikesResponse;
import com.genius.gitget.challenge.likes.facade.LikesFacade;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.annotation.GitGetUser;
import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.PagingResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    private final LikesFacade likesFacade;

    // 좋아요 목록 조회
    @GetMapping("/likes")
    public ResponseEntity<PagingResponse<UserLikesResponse>> getLikesListOfUser(
            Pageable pageable,
            @GitGetUser User user) {

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<UserLikesResponse> likesResponses = likesFacade.getLikesList(user, pageRequest);

        return ResponseEntity.ok().body(
                new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), likesResponses)
        );
    }

    // 좋아요 목록 추가
    @PostMapping("/likes")
    public ResponseEntity<SingleResponse<UserLikesAddResponse>> addLikes(
            @GitGetUser User user,
            @RequestBody UserLikesAddRequest userLikesAddRequest) {
        UserLikesAddResponse userLikesAddResponse = likesFacade.addLikes(user,
                userLikesAddRequest.getIdentifier(),
                userLikesAddRequest.getInstanceId());
        return ResponseEntity.ok().body(
                new SingleResponse<>(SuccessCode.CREATED.getStatus(), SuccessCode.CREATED.getMessage(),
                        userLikesAddResponse)
        );
    }

    // 좋아요 목록 삭제
    @DeleteMapping("/likes/{likesId}")
    public ResponseEntity<CommonResponse> deleteLikes(@GitGetUser User user,
                                                      @PathVariable(value = "likesId") Long likesId) {
        likesFacade.deleteLikes(user, likesId);
        return ResponseEntity.ok().body(
                new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage())
        );
    }
}
