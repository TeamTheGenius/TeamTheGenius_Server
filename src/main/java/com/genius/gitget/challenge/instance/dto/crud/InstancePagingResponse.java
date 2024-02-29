package com.genius.gitget.challenge.instance.dto.crud;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;

@Builder
public record InstancePagingResponse(Long topicId, Long instanceId, String title,
                                     LocalDateTime startedAt, LocalDateTime completedAt, FileResponse fileResponse) {
    public static InstancePagingResponse createByEntity(Instance instance, Optional<Files> files) throws IOException {
        return InstancePagingResponse.builder()
                .topicId(instance.getTopic().getId())
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .startedAt(instance.getStartedDate())
                .completedAt(instance.getCompletedDate())
                .fileResponse(convertToFileResponse(files))
                .build();
    }

    private static FileResponse convertToFileResponse(Optional<Files> files) {
        if (files.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(files.get());
    }
}
