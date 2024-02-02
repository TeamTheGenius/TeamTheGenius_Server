package com.genius.gitget.challenge.instance.dto.crud;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Builder
public record InstanceDetailResponse(Long topicId, Long instanceId, String title, String description, int pointPerPerson,
                                     String tags, String notice, LocalDateTime startedAt, LocalDateTime completedAt, FileResponse fileResponse) {
    public static InstanceDetailResponse createByEntity(Instance instance, Optional<Files> files) throws IOException {
        return InstanceDetailResponse.builder()
                .topicId(instance.getTopic().getId())
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .description(instance.getDescription())
                .pointPerPerson(instance.getPointPerPerson())
                .tags(instance.getTags())
                .notice(instance.getNotice())
                .startedAt(instance.getStartedDate())
                .completedAt(instance.getCompletedDate())
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
