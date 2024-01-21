package com.genius.gitget.instance.dto;

import com.genius.gitget.instance.domain.Progress;
import com.genius.gitget.topic.domain.Topic;

import java.time.LocalDateTime;

public record InstanceDTO(
    String title,
    String description,
    int participants,
    String tags,
    Integer pointPerPerson,
    Progress progress,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    Topic topic
    // Image
) {
}
