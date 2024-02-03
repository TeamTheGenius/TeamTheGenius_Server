package com.genius.gitget.challenge.instance.dto.crud;

import java.time.LocalDateTime;

public record InstanceCreateRequest(
        Long topicId,
        String title,
        String tags,
        String description,
        String notice,
        int pointPerPerson,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}
