package com.genius.gitget.topic.service;

import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.dto.TopicCreateRequest;
import com.genius.gitget.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TopicService {
    private final TopicRepository topicRepository;

    public Page<Topic> findTopics(Pageable pageable) {
        return topicRepository.findAllById(pageable);
    }

    public Topic findOne(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
    }

    @Transactional
    public Long create(Topic byTopicCreateDto) {
        Topic savedTopic = topicRepository.save(byTopicCreateDto);
        return savedTopic.getId();
    }

    @Transactional
    public void delete(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        topicRepository.delete(topic);
    }

    public Topic createTopicByTopicCreateRequest(TopicCreateRequest topicCreateRequest) {
        return Topic.builder()
                .title(topicCreateRequest.title())
                .description(topicCreateRequest.description())
                .tags(topicCreateRequest.tags())
                .pointPerPerson(topicCreateRequest.pointPerPerson())
                .notice(topicCreateRequest.notice())
                .build();
    }
}
