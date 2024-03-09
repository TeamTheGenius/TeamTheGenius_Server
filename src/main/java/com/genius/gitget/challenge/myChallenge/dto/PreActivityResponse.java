package com.genius.gitget.challenge.myChallenge.dto;

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
}
