package com.genius.gitget.admin.topic.service;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.dto.TopicCreateRequest;
import com.genius.gitget.admin.topic.dto.TopicDetailResponse;
import com.genius.gitget.admin.topic.dto.TopicPagingResponse;
import com.genius.gitget.admin.topic.dto.TopicUpdateRequest;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
        try {
            return TopicPagingResponse.createByEntity(topic, topic.getFiles());
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    // 토픽 상세정보 요청
    public TopicDetailResponse getTopicById(Long id) throws IOException {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        return TopicDetailResponse.createByEntity(topic, topic.getFiles());
    }

    // 토픽 생성 요청
    @Transactional
    public Long createTopic(TopicCreateRequest topicCreateRequest, MultipartFile multipartFile, String type) {
        Files uploadedFile = filesService.uploadFile(multipartFile, type);

        Topic topic = Topic.builder()
                .title(topicCreateRequest.title())
                .description(topicCreateRequest.description())
                .tags(topicCreateRequest.tags())
                .pointPerPerson(topicCreateRequest.pointPerPerson())
                .notice(topicCreateRequest.notice())
                .build();

        topic.setFiles(uploadedFile);

        Topic savedTopic = topicRepository.save(topic);

        return savedTopic.getId();
    }

    // 토픽 업데이트 요청
    @Transactional
    public void updateTopic(Long id, TopicUpdateRequest topicUpdateRequest, MultipartFile multipartFile, String type) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        Optional<Files> findTopicFile = topic.getFiles();
        Long findTopicFileId = findTopicFile.get().getId();

        filesService.updateFile(findTopicFileId, multipartFile);

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
