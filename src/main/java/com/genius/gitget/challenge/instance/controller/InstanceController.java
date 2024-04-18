package com.genius.gitget.challenge.instance.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.CREATED;
import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstanceDetailResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceIndexResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstancePagingResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateRequest;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.PagingResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class InstanceController {
    private final InstanceService instanceService;

    // 인스턴스 리스트 조회
    @GetMapping("/instance")
    public ResponseEntity<PagingResponse<InstancePagingResponse>> getAllInstances(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC, sort = "id") Pageable pageable) {
        Page<InstancePagingResponse> instances = instanceService.getAllInstances(pageable);

        return ResponseEntity.ok().body(
                new PagingResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), instances)
        );
    }

    // 특정 토픽에 대한 리스트 조회
    @GetMapping("topic/instances/{id}")
    public ResponseEntity<PagingResponse<InstancePagingResponse>> getAllInstancesOfSpecificTopic(
            @PageableDefault Pageable pageable, @PathVariable Long id) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("id"));
        Page<InstancePagingResponse> allInstancesOfSpecificTopic = instanceService.getAllInstancesOfSpecificTopic(
                pageRequest, id);

        return ResponseEntity.ok().body(
                new PagingResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(),
                        allInstancesOfSpecificTopic)
        );
    }


    // 인스턴스 단건 조회
    @GetMapping("/instance/{id}")
    public ResponseEntity<SingleResponse<InstanceDetailResponse>> getInstanceById(@PathVariable Long id) {
        InstanceDetailResponse instanceDetails = instanceService.getInstanceById(id);
        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), instanceDetails)
        );
    }

    // 인스턴스 생성
    @PostMapping("/instance")
    public ResponseEntity<SingleResponse<InstanceIndexResponse>> createInstance(
            @RequestBody InstanceCreateRequest instanceCreateRequest) {
        Long instanceId = instanceService.createInstance(instanceCreateRequest, LocalDate.now());
        InstanceIndexResponse instanceIndexResponse = new InstanceIndexResponse(instanceId);

        return ResponseEntity.ok().body(
                new SingleResponse<>(CREATED.getStatus(), CREATED.getMessage(), instanceIndexResponse)
        );
    }

    // 인스턴스 수정
    @PatchMapping("/instance/{id}")
    public ResponseEntity<SingleResponse<InstanceIndexResponse>> updateInstance(
            @PathVariable Long id,
            @RequestBody InstanceUpdateRequest instanceUpdateRequest) {

        Long instanceId = instanceService.updateInstance(id, instanceUpdateRequest);
        InstanceIndexResponse instanceIndexResponse = new InstanceIndexResponse(instanceId);
        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), instanceIndexResponse)
        );
    }

    // 인스턴스 삭제
    @DeleteMapping("/instance/{id}")
    public ResponseEntity<CommonResponse> deleteInstance(@PathVariable Long id) {
        instanceService.deleteInstance(id);
        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }
}