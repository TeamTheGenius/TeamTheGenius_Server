package com.genius.gitget.topic.service;

import com.genius.gitget.topic.dto.TopicDetailResponse;
import com.genius.gitget.topic.dto.TopicUpdateRequest;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.dto.TopicCreateRequest;
import com.genius.gitget.topic.dto.TopicPagingResponse;
import com.genius.gitget.topic.repository.TopicRepository;
import com.genius.gitget.util.exception.BusinessException;
import com.genius.gitget.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {
    private final TopicRepository topicRepository;

    // 토픽 리스트 요청
    public Page<TopicPagingResponse> getAllTopics(Pageable pageable) {
        Page<Topic> topics = topicRepository.findAllById(pageable);
        return topics.map(topic -> new TopicPagingResponse(topic.getId(), topic.getTitle()));
    }

    // 토픽 상세정보 요청
    public TopicDetailResponse getTopicById(Long id) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        return new TopicDetailResponse(topic.getId(), topic.getTitle(), topic.getTags(), topic.getDescription(), topic.getPointPerPerson());
    }

    // 토픽 생성 요청
    @Transactional
    public void createTopic(TopicCreateRequest topicCreateRequest) {
        Topic topic = Topic.builder()
                .title(topicCreateRequest.title())
                .description(topicCreateRequest.description())
                .tags(topicCreateRequest.tags())
                .pointPerPerson(topicCreateRequest.pointPerPerson())
                // 이미지
                // 유의사항
                .build();
        topicRepository.save(topic);
    }

    @Transactional
    public void updateTopic(Long id, TopicUpdateRequest topicUpdateRequest) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        // 서버에서 한번 더 검사
        boolean hasInstance = !topic.getInstanceList().isEmpty();
        if (hasInstance) {
            topic.updateExistInstance(topicUpdateRequest.description());
        } else {
            topic.createInstance(topicUpdateRequest.title(), topicUpdateRequest.description(), topicUpdateRequest.tags(), topicUpdateRequest.pointPerPerson());
        }
        topicRepository.save(topic);
    }

    // 토픽 삭제 요청
    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        topicRepository.delete(topic);
    }
}
