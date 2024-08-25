package com.genius.gitget.challenge.instance.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.instance.dto.home.HomeInstanceResponse;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchRequest;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.facade.InstanceHomeFacade;
import com.genius.gitget.global.page.LimitedSizePagination;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.PagingResponse;
import com.genius.gitget.global.util.response.dto.SlicingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
public class InstanceHomeController {
    private final InstanceHomeFacade instanceHomeFacade;

    @PostMapping("/search")
    @LimitedSizePagination
    public ResponseEntity<PagingResponse<InstanceSearchResponse>> searchInstances(
            @RequestBody InstanceSearchRequest instanceSearchRequest, Pageable pageable) {

        Page<InstanceSearchResponse> searchResults
                = instanceHomeFacade.searchInstancesByKeywordAndProgress(instanceSearchRequest, pageable);

        return ResponseEntity.ok().body(
                new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), searchResults)
        );
    }

    @GetMapping("/recommend")
    @LimitedSizePagination
    public ResponseEntity<SlicingResponse<HomeInstanceResponse>> getRecommendInstances(
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Direction.DESC, "participantCount"));

        Slice<HomeInstanceResponse> recommendations = instanceHomeFacade.recommendInstances(
                userPrincipal.getUser(), pageRequest);
        return ResponseEntity.ok().body(
                new SlicingResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), recommendations)
        );
    }

    @GetMapping("/popular")
    @LimitedSizePagination
    public ResponseEntity<SlicingResponse<HomeInstanceResponse>> getPopularInstances(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Direction.DESC, "participantCount"));

        Slice<HomeInstanceResponse> recommendations = instanceHomeFacade.findInstancesByCondition(
                pageRequest);
        return ResponseEntity.ok().body(
                new SlicingResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), recommendations)
        );
    }

    @GetMapping("/latest")
    @LimitedSizePagination
    public ResponseEntity<SlicingResponse<HomeInstanceResponse>> getLatestInstances(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Direction.DESC, "startedDate"));

        Slice<HomeInstanceResponse> recommendations = instanceHomeFacade.findInstancesByCondition(
                pageRequest);
        return ResponseEntity.ok().body(
                new SlicingResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), recommendations)
        );
    }
}