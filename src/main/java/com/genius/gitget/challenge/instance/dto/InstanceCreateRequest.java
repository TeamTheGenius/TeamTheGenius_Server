package com.genius.gitget.challenge.instance.dto;

import java.time.LocalDateTime;

public record InstanceCreateRequest(
        Long topicId,
        String title,
        String tags,
        String description,
        // TODO 이미지
        // TODO 유의사항
        int pointPerPerson,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}
