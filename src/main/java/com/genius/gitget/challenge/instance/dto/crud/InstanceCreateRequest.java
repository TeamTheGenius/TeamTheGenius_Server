package com.genius.gitget.challenge.instance.dto.crud;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
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
