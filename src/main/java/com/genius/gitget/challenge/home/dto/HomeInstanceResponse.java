package com.genius.gitget.challenge.home.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.Builder;

@Builder
public record HomeInstanceResponse(
        String title,
        int participantCnt,
        int pointPerPerson,
        FileResponse fileResponse
) {
    public static HomeInstanceResponse createByEntity(Instance instance, Optional<Files> files) throws IOException {
        return HomeInstanceResponse.builder()
                .title(instance.getTitle())
                .participantCnt(instance.getParticipantCnt())
                .pointPerPerson(instance.getPointPerPerson())
                .fileResponse(convertToFileResponse(files))
                .build();
    }

    private static FileResponse convertToFileResponse(Optional<Files> files) throws IOException {
        if (files.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(files.get());
    }
}
