package com.genius.gitget.challenge.home.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import lombok.Builder;

@Builder
public record RecommendPagingResponse(
        String title,
        int participantCnt,
        int pointPerPerson

) {
    public static RecommendPagingResponse createByEntity(Instance instance) {
        return RecommendPagingResponse.builder()
                .title(instance.getTitle())
                .participantCnt(instance.getParticipantCnt())
                .pointPerPerson(instance.getPointPerPerson())
                .build();
    }
}
