package com.genius.gitget.challenge.instance.dto.crud;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InstanceCreateRequest(
        Long topicId,
        String title,
        String tags,
        String description,
        String notice,
        int pointPerPerson,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        String certificationMethod) {
    public static Instance from(InstanceCreateRequest instanceCreateRequest) {
        return Instance.builder()
                .title(instanceCreateRequest.title())
                .tags(instanceCreateRequest.tags())
                .description(instanceCreateRequest.description())
                .pointPerPerson(instanceCreateRequest.pointPerPerson())
                .notice(instanceCreateRequest.notice())
                .startedDate(instanceCreateRequest.startedAt())
                .completedDate(instanceCreateRequest.completedAt())
                .certificationMethod(instanceCreateRequest.certificationMethod())
                .progress(Progress.PREACTIVITY)
                .build();
    }
}
