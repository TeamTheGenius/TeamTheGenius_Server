package com.genius.gitget.topic.facade;

import com.genius.gitget.topic.dto.TopicCreateRequest;
import com.genius.gitget.topic.dto.TopicDetailResponse;
import com.genius.gitget.topic.dto.TopicPagingResponse;
import com.genius.gitget.topic.dto.TopicUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopicFacade {

    Page<TopicPagingResponse> findTopics(Pageable pageable);

    TopicDetailResponse findOne(Long id);

    Long create(TopicCreateRequest topicCreateRequest);

    Long update(Long id, TopicUpdateRequest topicUpdateRequest);

    void delete(Long id);
}
