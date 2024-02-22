package com.genius.gitget.challenge.certification.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import lombok.Builder;

@Builder
public record InstancePreviewResponse(
        Long instanceId,
        String title,
        int participantCount,
        String period,
        int pointPerPerson,
        String certificationMethod
) {

    public static InstancePreviewResponse createByEntity(Instance instance) {
        return InstancePreviewResponse.builder()
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .participantCount(instance.getParticipantCount())
                .period(instance.getStartedDate().toLocalDate() + " ~ " + instance.getCompletedDate().toLocalDate())
                .pointPerPerson(instance.getPointPerPerson())
                .certificationMethod(instance.getCertificationMethod())
                .build();
    }
}
