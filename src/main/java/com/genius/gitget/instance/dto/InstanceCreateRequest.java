package com.genius.gitget.instance.dto;

import java.time.LocalDateTime;

public record InstanceCreateRequest(
        Long topicId,
        Long instanceId,
        String title,
        String tags,
        String description,
        //이미지
        //유의사항
        int pointPerPerson,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}
