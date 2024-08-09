package com.genius.gitget.topic.facade;

import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesManager;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.dto.TopicCreateRequest;
import com.genius.gitget.topic.dto.TopicDetailResponse;
import com.genius.gitget.topic.dto.TopicPagingResponse;
import com.genius.gitget.topic.dto.TopicUpdateRequest;
import com.genius.gitget.topic.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional
public class TopicFacadeService implements TopicFacade {

    private final FilesManager filesManager;
    private final TopicService topicService;


    @Override
    public Page<TopicPagingResponse> findTopics(Pageable pageable) {
        Page<Topic> findTopics = topicService.findTopics(pageable);
        return findTopics.map(this::convertToTopicPagingResponseDto);
    }

    @Override
    public TopicDetailResponse findOne(Long id) {
        Topic findTopic = topicService.findOne(id);
        FileResponse fileResponse = filesManager.convertToFileResponse(findTopic.getFiles());
        return TopicDetailResponse.of(findTopic, fileResponse);
    }

    @Override
    public Long create(TopicCreateRequest topicCreateRequest) {
        Topic topic = TopicCreateRequest.from(topicCreateRequest);
        return topicService.create(topic);
    }

    @Override
    public Long update(Long id, TopicUpdateRequest topicUpdateRequest) {
        Topic topic = topicService.findOne(id);

        if (!topic.getInstanceList().isEmpty()) {
            topic.updateExistInstance(topicUpdateRequest.description());
            return topicService.create(topic);
        }

        topic.updateNotExistInstance(topicUpdateRequest.title(), topicUpdateRequest.description(),
                topicUpdateRequest.tags(), topicUpdateRequest.notice(), topicUpdateRequest.pointPerPerson());
        return topicService.create(topic);
    }

    @Override
    public void delete(Long id) {
        topicService.delete(id);
    }

    private TopicPagingResponse convertToTopicPagingResponseDto(Topic topic) {
        FileResponse fileResponse = filesManager.convertToFileResponse(topic.getFiles());
        return TopicPagingResponse.of(topic, fileResponse);
    }
}
