package com.genius.gitget.topic.dto;

import lombok.Builder;

@Builder
public record TopicUpdateRequest(
        String title,
        String tags,
        String description,
        int pointPerPerson,
        String notice
) {
}
