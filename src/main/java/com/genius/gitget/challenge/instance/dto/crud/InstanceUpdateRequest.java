package com.genius.gitget.challenge.instance.dto.crud;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InstanceUpdateRequest(
        Long topicId,
        String description,
        String notice,
        int pointPerPerson,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}
