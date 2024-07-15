package com.genius.gitget.topic.dto;

import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.topic.domain.Topic;
import lombok.Builder;

@Builder
public record TopicPagingResponse(Long topicId, String title, FileResponse fileResponse) {

    public static TopicPagingResponse of(Topic topic, FileResponse fileResponse) {
        return TopicPagingResponse.builder()
                .topicId(topic.getId())
                .title(topic.getTitle())
                .fileResponse(fileResponse)
                .build();
    }
}
