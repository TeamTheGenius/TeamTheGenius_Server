package com.genius.gitget.topic.controller;

import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TopicController {

    private final TopicService topicService;

    // 토픽 리스트 요청
    @GetMapping("/admin/topic")
    public List<Topic> getAllTopics() {
        return topicService.getAllTopics();
    }

    // 토픽 상세 정보 요청
    @GetMapping("/admin/topic/{topicId}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long topicId) {
        Topic topic = topicService.getTopicById(topicId);
        return ResponseEntity.ok(topic);
    }

    // 토픽 생성 요청
    @PostMapping("/admin/topic")
    public ResponseEntity<Topic> createTopic(@Valid Topic topic) {
        Topic createdTopic = topicService.createTopic(topic);
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    // 토픽 수정 요청
    @PatchMapping("/admin/topic/{topicId}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long topicId, Topic topicDetails ) {
        Topic updatedTopic = topicService.updateTopic(topicId, topicDetails);
        return ResponseEntity.ok(updatedTopic);
    }

    // 토픽 삭제 요청
    @DeleteMapping("/admin/topic/{topicId}")
    public ResponseEntity<HttpStatus> deleteTopic(@PathVariable Long topicId) {
        topicService.deleteTopic(topicId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}