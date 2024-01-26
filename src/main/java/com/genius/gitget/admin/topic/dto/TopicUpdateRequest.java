package com.genius.gitget.admin.topic.dto;

public record TopicUpdateRequest(
        String title,
        String tags,
        String description,
        int pointPerPerson
        // 이미지
        // 유의사항
) {
}
