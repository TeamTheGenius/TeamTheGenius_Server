package com.genius.gitget.challenge.instance.dto.crud;

import java.time.LocalDateTime;

public record InstancePagingResponse(
        Long topicId,
        Long instanceId,
        String title,
        // 이미지
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}
