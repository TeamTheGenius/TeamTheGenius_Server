package com.genius.gitget.topic.dto;

import com.genius.gitget.topic.domain.Topic;
import lombok.Builder;

@Builder
public record TopicCreateRequest(
        String title,
        String tags,
        String description,
        int pointPerPerson,
        String notice
) {
    public static Topic from(TopicCreateRequest topicCreateRequest) {
        return Topic.builder()
                .title(topicCreateRequest.title())
                .description(topicCreateRequest.description())
                .tags(topicCreateRequest.tags())
                .pointPerPerson(topicCreateRequest.pointPerPerson())
                .notice(topicCreateRequest.notice())
                .build();
    }
}
