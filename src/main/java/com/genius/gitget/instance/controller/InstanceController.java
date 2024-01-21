package com.genius.gitget.instance.controller;

import com.genius.gitget.instance.domain.Instance;
import com.genius.gitget.instance.dto.InstanceDTO;
import com.genius.gitget.instance.service.InstanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/instance")
@RequiredArgsConstructor
public class InstanceController {
    private final InstanceService instanceService;

    // 인스턴스 리스트 조회
    // @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size, @RequestParam Optional<String> sortBy
    @GetMapping("/")
    public Page<Instance> getAllInstances() {
        return instanceService.getAllInstances();
    }

    // 인스턴스 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<Instance> getInstanceById(@PathVariable Long id) {
        Instance instanceById = instanceService.getInstanceById(id);
        return ResponseEntity.ok(instanceById);
    }

    // 인스턴스 생성
    @PostMapping("/{topicId}")
    public ResponseEntity<Instance> createInstance(@PathVariable Long topicId, @Valid @RequestBody InstanceDTO instanceDTO) {
        Instance createdInstance = instanceService.createInstance(topicId, instanceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInstance);
    }

    // 인스턴스 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Instance> updateInstance(@PathVariable Long id, @RequestBody @Valid InstanceDTO instanceDTO) {
        Instance updatedInstance = instanceService.updateInstance(id, instanceDTO);
        return ResponseEntity.ok(updatedInstance);
    }

    // 인스턴스 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstance(@PathVariable Long id) {
        instanceService.deleteInstance(id);
        return ResponseEntity.noContent().build();
    }
}
