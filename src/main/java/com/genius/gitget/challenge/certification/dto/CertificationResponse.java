package com.genius.gitget.challenge.certification.dto;

import com.genius.gitget.challenge.instance.domain.Instance;
import lombok.Builder;

@Builder
public record CertificationResponse(
        Long instanceId,
        int participantCount,
        String period,
        int pointPerPerson,
        String repositoryName,
        String certificationMethod
) {

    public static CertificationResponse createByEntity(Instance instance, String repositoryName) {
        return CertificationResponse.builder()
                .instanceId(instance.getId())
                .participantCount(instance.getParticipantCount())
                .period(instance.getStartedDate().toLocalDate() + " ~ " + instance.getCompletedDate().toLocalDate())
                .pointPerPerson(instance.getPointPerPerson())
                .repositoryName(repositoryName)
                .certificationMethod(instance.getCertificationMethod())
                .build();
    }
}
