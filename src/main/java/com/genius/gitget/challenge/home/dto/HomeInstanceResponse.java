package com.genius.gitget.challenge.home.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import lombok.Builder;

@Builder
public record HomeInstanceResponse(
        String title,
        int participantCnt,
        int pointPerPerson,
        String encodedImage
) {
    public static HomeInstanceResponse createByEntity(Instance instance) {
        return HomeInstanceResponse.builder()
                .title(instance.getTitle())
                .participantCnt(instance.getParticipantCnt())
                .pointPerPerson(instance.getPointPerPerson())
                .build();
    }
}
