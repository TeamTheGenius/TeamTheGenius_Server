package com.genius.gitget.challenge.instance.dto.crud;

import java.time.LocalDateTime;

public record InstanceUpdateRequest(
        Long topicId,
        String description,
        int pointPerPerson,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}
