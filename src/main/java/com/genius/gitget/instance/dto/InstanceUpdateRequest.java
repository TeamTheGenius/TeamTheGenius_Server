package com.genius.gitget.instance.dto;

import java.time.LocalDateTime;

public record InstanceUpdateRequest (
        Long topicId,
        Long instanceId,
        String description,
        int pointPerPerson,
        LocalDateTime startedAt,
        LocalDateTime completedAt
){
}
