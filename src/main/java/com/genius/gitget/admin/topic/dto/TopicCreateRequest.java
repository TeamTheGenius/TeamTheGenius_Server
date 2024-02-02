package com.genius.gitget.admin.topic.dto;

public record TopicCreateRequest(
        String title,
        String tags,
        String description,
        int pointPerPerson,
        String notice
) {
}
