package com.genius.gitget.challenge.instance.dto.crud;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.dto.FileResponse;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InstanceDetailResponse(Long topicId, Long instanceId, String title, String description,
                                     int pointPerPerson,
                                     String tags, String notice, LocalDateTime startedAt, LocalDateTime completedAt,
                                     String certificationMethod,
                                     FileResponse fileResponse) {
    public static InstanceDetailResponse createByEntity(Instance instance, FileResponse fileResponse) {
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
                .certificationMethod(instance.getCertificationMethod())
                .fileResponse(fileResponse)
                .build();
    }
}
