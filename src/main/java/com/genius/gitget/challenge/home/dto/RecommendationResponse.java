package com.genius.gitget.challenge.home.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import lombok.Builder;

@Builder
public record RecommendationResponse(
        String title,
        int participantCnt,
        int pointPerPerson,
        String encodedImage
) {
    public static RecommendationResponse createByEntity(Instance instance) {
        return RecommendationResponse.builder()
                .title(instance.getTitle())
                .participantCnt(instance.getParticipantCnt())
                .pointPerPerson(instance.getPointPerPerson())
                .build();
    }
}
