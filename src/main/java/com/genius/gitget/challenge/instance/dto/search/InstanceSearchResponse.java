package com.genius.gitget.challenge.instance.dto.search;

import com.genius.gitget.challenge.instance.domain.Instance;

public record InstanceSearchResponse(
        Long topicId,
        Long instanceId,
        String keyword,
        int pointPerPerson,
        int participantCount
) {
    public InstanceSearchResponse(Instance instance) {
        this(instance.getTopic().getId(),
                instance.getId(),
                instance.getTitle(),
                instance.getPointPerPerson(),
                instance.getParticipantCnt());
    }
}
