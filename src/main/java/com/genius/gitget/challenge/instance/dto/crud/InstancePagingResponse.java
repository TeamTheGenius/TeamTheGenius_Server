package com.genius.gitget.challenge.instance.dto.crud;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.dto.FileResponse;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InstancePagingResponse(
        Long topicId,
        Long instanceId,
        String title,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        FileResponse fileResponse) {
    public static InstancePagingResponse createByEntity(Instance instance, FileResponse fileResponse) {
        return InstancePagingResponse.builder()
                .topicId(instance.getTopic().getId())
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .startedAt(instance.getStartedDate())
                .completedAt(instance.getCompletedDate())
                .fileResponse(fileResponse)
                .build();
    }
}
