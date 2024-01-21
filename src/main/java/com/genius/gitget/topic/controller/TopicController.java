package com.genius.gitget.topic.controller;

import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.dto.TopicDTO;
import com.genius.gitget.topic.service.TopicService;
import com.genius.gitget.util.response.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topic")
public class TopicController {

    private final TopicService topicService;


    // 토픽 리스트 요청
    @GetMapping("/")
    public ResponseEntity<Page<Topic>> getAllTopics(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, sortBy);
        Page<Topic> topics = topicService.getAllTopics(pageRequest);

        return ResponseEntity.ok(topics);
    }

    // 토픽 상세 정보 요청
    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long id) {
        Topic topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }

    // 토픽 생성 요청
    @PostMapping("/")
    public ResponseEntity<Topic> createTopic(@RequestBody @Valid TopicDTO topicDTO) {
        Topic createdTopic = topicService.createTopic(topicDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTopic);
    }

    // 토픽 수정 요청
    @PatchMapping("/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @RequestBody @Valid TopicDTO topicDTO) {
        Topic updatedTopic = topicService.updateTopic(id, topicDTO);
        return ResponseEntity.ok(updatedTopic);
    }

    // 토픽 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}