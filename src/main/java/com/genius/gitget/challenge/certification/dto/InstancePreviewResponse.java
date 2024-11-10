package com.genius.gitget.challenge.certification.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.dto.FileResponse;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record InstancePreviewResponse(
        Long instanceId,
        String title,
        int participantCount,
        LocalDate startDate,
        LocalDate completedDate,
        int pointPerPerson,
        String certificationMethod,
        FileResponse fileResponse
) {

    public static InstancePreviewResponse createByEntity(Instance instance, FileResponse fileResponse) {
        return InstancePreviewResponse.builder()
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .participantCount(instance.getParticipantCount())
                .startDate(instance.getStartedDate().toLocalDate())
                .completedDate(instance.getCompletedDate().toLocalDate())
                .pointPerPerson(instance.getPointPerPerson())
                .certificationMethod(instance.getCertificationMethod())
                .fileResponse(fileResponse)
                .build();
    }
}
