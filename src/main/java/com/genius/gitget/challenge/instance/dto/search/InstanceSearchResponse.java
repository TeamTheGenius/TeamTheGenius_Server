package com.genius.gitget.challenge.instance.dto.search;

public record InstanceSearchResponse(
        Long topicId,
        Long instanceId,
        String keyword,
        int pointPerPerson,
        int participantCount
) {
}
