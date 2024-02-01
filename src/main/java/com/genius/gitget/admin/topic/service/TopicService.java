package com.genius.gitget.admin.topic.service;

import com.genius.gitget.admin.topic.dto.TopicCreateRequest;
import com.genius.gitget.admin.topic.dto.TopicPagingResponse;
import com.genius.gitget.admin.topic.dto.TopicUpdateRequest;
import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.dto.TopicDetailResponse;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {
    private final TopicRepository topicRepository;
    public final FilesService filesService;

    // 토픽 리스트 요청
    public Page<TopicPagingResponse> getAllTopics(Pageable pageable) {
        Page<Topic> topics = topicRepository.findAllById(pageable);
        return topics.map(topic -> new TopicPagingResponse(topic.getId(), topic.getTitle()));
    }

    // 토픽 상세정보 요청
    public TopicDetailResponse getTopicById(Long id) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));
        return new TopicDetailResponse(topic.getId(), topic.getTitle(), topic.getTags(), topic.getDescription(),
                topic.getPointPerPerson());
    }

    // 토픽 생성 요청
    @Transactional
    public Long createTopic(TopicCreateRequest topicCreateRequest, MultipartFile multipartFile, String type) throws IOException {
        // TODO 이미지 타입 체크 필요

        Files uploadedFile = filesService.uploadFile(multipartFile, type);

        Topic topic = Topic.builder()
                .title(topicCreateRequest.title())
                .description(topicCreateRequest.description())
                .tags(topicCreateRequest.tags())
                .pointPerPerson(topicCreateRequest.pointPerPerson())
                // 이미지
                // 유의사항
                .build();

        topic.setFiles(uploadedFile);

        Topic savedTopic = topicRepository.save(topic);

        // 생성된 토픽을 ID로 조회 가능하도록 수정 (01/29)
        return savedTopic.getId();
    }

    @Transactional
    public void updateTopic(Long id, TopicUpdateRequest topicUpdateRequest, MultipartFile multipartFile, String type) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        // 서버에서 한번 더 검사
        boolean hasInstance = !topic.getInstanceList().isEmpty();
        if (hasInstance) {
            topic.updateExistInstance(topicUpdateRequest.description());
        } else {
            topic.updateNotExistInstance(topicUpdateRequest.title(), topicUpdateRequest.description(),
                    topicUpdateRequest.tags(), topicUpdateRequest.pointPerPerson());
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
