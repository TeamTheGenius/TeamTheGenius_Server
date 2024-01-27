package com.genius.gitget.admin.topic.dto;

public record TopicCreateRequest(
        String title,
        String tags,
        String description,
        int pointPerPerson
        // 이미지
        // 유의사항
) {
}
