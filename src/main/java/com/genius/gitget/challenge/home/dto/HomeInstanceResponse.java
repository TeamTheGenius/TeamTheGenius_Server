package com.genius.gitget.challenge.home.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;

@Builder
public record HomeInstanceResponse(
        String title,
        int participantCnt,
        int pointPerPerson,
        FileResponse fileResponse
) {
    public static HomeInstanceResponse createByEntity(Instance instance, FileResponse fileResponse) {
        return HomeInstanceResponse.builder()
                .title(instance.getTitle())
                .participantCnt(instance.getParticipantCnt())
                .pointPerPerson(instance.getPointPerPerson())
                .fileResponse(fileResponse)
                .build();
    }
}
