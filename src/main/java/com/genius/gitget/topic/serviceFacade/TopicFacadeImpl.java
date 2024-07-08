package com.genius.gitget.topic.serviceFacade;

import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
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
public class TopicFacadeImpl implements TopicFacade {

    private final FilesService filesService;
    private final TopicService topicService;

    @Override
    public Page<TopicPagingResponse> findTopics(Pageable pageable) {
        Page<Topic> allTopicById = topicService.findTopics(pageable);
        return allTopicById.map(this::convertToTopicPagingResponseDto);
    }

    @Override
    public TopicDetailResponse findOne(Long id) {
        Topic topic = topicService.findOne(id);
        FileResponse fileResponse = filesService.convertToFileResponse(topic.getFiles());
        return TopicDetailResponse.createByEntity(topic, fileResponse);
    }

    @Override
    public Long create(TopicCreateRequest topicCreateRequest) {
        Topic byTopicCreateDto = topicService.createTopicByTopicCreateRequest(topicCreateRequest);
        return topicService.create(byTopicCreateDto);
    }

    @Override
    public Long update(Long id, TopicUpdateRequest topicUpdateRequest) {
        Topic topic = topicService.findOne(id);

        boolean hasInstance = !topic.getInstanceList().isEmpty();
        if (hasInstance) {
            topic.updateExistInstance(topicUpdateRequest.description());
        } else {
            topic.updateNotExistInstance(topicUpdateRequest.title(), topicUpdateRequest.description(),
                    topicUpdateRequest.tags(), topicUpdateRequest.notice(), topicUpdateRequest.pointPerPerson());
        }
        return topicService.create(topic);
    }

    @Override
    public void delete(Long id) {
        topicService.delete(id);
    }

    private TopicPagingResponse convertToTopicPagingResponseDto(Topic topic) {
        FileResponse fileResponse = filesService.convertToFileResponse(topic.getFiles());
        return TopicPagingResponse.createByEntity(topic, fileResponse);
    }
}
