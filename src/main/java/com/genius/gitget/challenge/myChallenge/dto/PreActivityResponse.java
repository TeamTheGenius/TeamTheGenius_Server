package com.genius.gitget.challenge.myChallenge.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;

@Builder
public record PreActivityResponse(
        Long instanceId,
        String title,
        int participantCount,
        int pointPerPerson,
        int remainDays,
        FileResponse fileResponse
) {

    public static PreActivityResponse of(Instance instance, int remainDays, FileResponse fileResponse) {
        return PreActivityResponse.builder()
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .participantCount(instance.getParticipantCount())
                .pointPerPerson(instance.getPointPerPerson())
                .remainDays(remainDays)
                .fileResponse(fileResponse)
                .build();
    }
}
