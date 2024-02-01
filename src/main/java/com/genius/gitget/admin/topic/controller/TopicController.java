package com.genius.gitget.admin.topic.controller;

import com.genius.gitget.admin.topic.dto.TopicCreateRequest;
import com.genius.gitget.admin.topic.dto.TopicPagingResponse;
import com.genius.gitget.admin.topic.dto.TopicUpdateRequest;
import com.genius.gitget.admin.topic.dto.TopicDetailResponse;
import com.genius.gitget.admin.topic.service.TopicService;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.PagingResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/topic")
public class TopicController {

    private final TopicService topicService;

    // 토픽 리스트 요청
    @GetMapping
    public ResponseEntity<PagingResponse<TopicPagingResponse>> getAllTopics(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<TopicPagingResponse> allTopics = topicService.getAllTopics(pageable);

        return ResponseEntity.ok().body(
                new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), allTopics)
        );
    }

    // 토픽 상세 정보 요청
    @GetMapping("/{id}")
    public ResponseEntity<SingleResponse<TopicDetailResponse>> getTopicById(@PathVariable Long id) {
        TopicDetailResponse topicDetail = topicService.getTopicById(id);
        return ResponseEntity.ok().body(
                new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), topicDetail)
        );
    }

    // 토픽 생성 요청
    @PostMapping
    public ResponseEntity<CommonResponse> createTopic(@RequestPart(value = "data") TopicCreateRequest topicCreateRequest,
                                                      @RequestPart(value = "files", required = false) MultipartFile multipartFile, @RequestPart(value = "type") String type) throws IOException {
        topicService.createTopic(topicCreateRequest, multipartFile, type);
        return ResponseEntity.ok().body(
                new CommonResponse(SuccessCode.CREATED.getStatus(), SuccessCode.CREATED.getMessage())
        );
    }

    // 토픽 수정 요청
    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponse> updateTopic(@PathVariable Long id, @RequestPart(value = "data") TopicUpdateRequest topicUpdateRequest,
                                                      @RequestPart(value = "files", required = false) MultipartFile multipartFile, @RequestPart(value = "type") String type) throws IOException {
        topicService.updateTopic(id, topicUpdateRequest, multipartFile, type);
        return ResponseEntity.ok().body(
                new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage())
        );
    }

    // 토픽 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok().body(
                new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage())
        );
    }
}