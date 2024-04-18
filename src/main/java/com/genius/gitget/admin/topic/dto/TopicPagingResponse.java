package com.genius.gitget.admin.topic.dto;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;

@Builder
public record TopicPagingResponse(Long topicId, String title, FileResponse fileResponse) {

    public static TopicPagingResponse createByEntity(Topic topic, FileResponse fileResponse) {
        return TopicPagingResponse.builder()
                .topicId(topic.getId())
                .title(topic.getTitle())
                .fileResponse(fileResponse)
                .build();
    }
}
