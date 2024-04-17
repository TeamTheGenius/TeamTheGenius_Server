package com.genius.gitget.admin.topic.service;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.dto.TopicCreateRequest;
import com.genius.gitget.admin.topic.dto.TopicDetailResponse;
import com.genius.gitget.admin.topic.dto.TopicPagingResponse;
import com.genius.gitget.admin.topic.dto.TopicUpdateRequest;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
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
    private final FilesService filesService;

    // 토픽 리스트 요청
    public Page<TopicPagingResponse> getAllTopics(Pageable pageable) {
        Page<Topic> topics = topicRepository.findAllById(pageable);
        return topics.map(this::mapToTopicPagingResponse);
    }

    private TopicPagingResponse mapToTopicPagingResponse(Topic topic) {
        FileResponse fileResponse = filesService.convertToFileResponse(topic.getFiles());
        return TopicPagingResponse.createByEntity(topic, fileResponse);
    }

    // 토픽 상세정보 요청
    public TopicDetailResponse getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        FileResponse fileResponse = filesService.convertToFileResponse(topic.getFiles());
        return TopicDetailResponse.createByEntity(topic, fileResponse);
    }

    // 토픽 생성 요청
    @Transactional
    public Long createTopic(TopicCreateRequest topicCreateRequest) {
        Topic topic = Topic.builder()
                .title(topicCreateRequest.title())
                .description(topicCreateRequest.description())
                .tags(topicCreateRequest.tags())
                .pointPerPerson(topicCreateRequest.pointPerPerson())
                .notice(topicCreateRequest.notice())
                .build();

        Topic savedTopic = topicRepository.save(topic);

        return savedTopic.getId();
    }

    // 토픽 업데이트 요청
    @Transactional
    public void updateTopic(Long id, TopicUpdateRequest topicUpdateRequest) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        // 서버에서 한번 더 검사
        boolean hasInstance = !topic.getInstanceList().isEmpty();
        if (hasInstance) {
            topic.updateExistInstance(topicUpdateRequest.description());
        } else {
            topic.updateNotExistInstance(topicUpdateRequest.title(), topicUpdateRequest.description(),
                    topicUpdateRequest.tags(), topicUpdateRequest.notice(), topicUpdateRequest.pointPerPerson());
        }
        topicRepository.save(topic);
    }

    // 토픽 삭제 요청
    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        topicRepository.delete(topic);
    }
}
