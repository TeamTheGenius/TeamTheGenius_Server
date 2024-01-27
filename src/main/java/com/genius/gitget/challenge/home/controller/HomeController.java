package com.genius.gitget.challenge.home.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.home.dto.RecommendationResponse;
import com.genius.gitget.challenge.home.service.HomeService;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.response.dto.SlicingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/recommend")
    public ResponseEntity<SlicingResponse<RecommendationResponse>> getRecommendations(
            @PageableDefault(size = 10, sort = "participantCnt", direction = Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Slice<RecommendationResponse> recommendations = homeService.getRecommendations(
                userPrincipal.getUser(), pageable);
        return ResponseEntity.ok().body(
                new SlicingResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), recommendations)
        );
    }
}
