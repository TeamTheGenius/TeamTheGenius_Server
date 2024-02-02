package com.genius.gitget.admin.topic.dto;

public record TopicUpdateRequest(
        String title,
        String tags,
        String description,
        int pointPerPerson,
        String notice
) {
}
