package com.genius.gitget.challenge.instance.dto.crud;

import java.time.LocalDateTime;

public record InstanceDetailResponse(
        Long topicId,
        Long instanceId,
        String title,
        String description,
        int pointPerPerson,
        String tags,
        // 이미지
        // 유의사항
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {

}
