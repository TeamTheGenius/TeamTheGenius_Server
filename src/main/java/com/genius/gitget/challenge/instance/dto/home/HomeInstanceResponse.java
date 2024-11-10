package com.genius.gitget.challenge.instance.dto.home;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;

@Builder
public record HomeInstanceResponse(
        Long instanceId,
        String title,
        int participantCnt,
        int pointPerPerson,
        FileResponse fileResponse
) {
    public static HomeInstanceResponse createByEntity(Instance instance, FileResponse fileResponse) {
        return HomeInstanceResponse.builder()
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .participantCnt(instance.getParticipantCount())
                .pointPerPerson(instance.getPointPerPerson())
                .fileResponse(fileResponse)
                .build();
    }
}
