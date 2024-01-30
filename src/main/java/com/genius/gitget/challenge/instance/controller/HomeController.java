package com.genius.gitget.challenge.instance.controller;

import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchRequest;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.service.InstanceSearchService;
import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.PagingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HomeController {
    private final InstanceSearchService instanceSearchService;

    @PostMapping("/challenges")
    public ResponseEntity<PagingResponse<InstanceSearchResponse>> searchInstances(@RequestBody InstanceSearchRequest instanceSearchRequest, Pageable pageable) {

        System.out.println("instanceSearchRequest = " + instanceSearchRequest);
        Page<InstanceSearchResponse> searchResults
                = instanceSearchService.searchInstances(instanceSearchRequest.getKeyword(), instanceSearchRequest.getProgress(), pageable);

        System.out.println("searchResults = " + searchResults);
        return ResponseEntity.ok().body(
                new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), searchResults)
        );
    }
}
