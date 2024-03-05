package com.genius.gitget.challenge.instance.dto.detail;

import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.global.file.dto.FileResponse;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record InstanceResponse(
        Long instanceId,
        String title,
        int remainDays,
        LocalDate startedDate,
        LocalDate completedDate,
        int participantCount,
        int pointPerPerson,
        String description,
        String notice,
        String certificationMethod,
        JoinStatus joinStatus,
        int hitCount,
        FileResponse fileResponse
) {

    public static InstanceResponse createByEntity(Instance instance, JoinStatus joinStatus) {
        LocalDate startedLocalDate = instance.getStartedDate().toLocalDate();
        LocalDate completedLocalDate = instance.getCompletedDate().toLocalDate();
        return InstanceResponse.builder()
                .title(instance.getTitle())
                .instanceId(instance.getId())
                .remainDays(DateUtil.getRemainDaysToStart(startedLocalDate, LocalDate.now()))
                .startedDate(startedLocalDate)
                .completedDate(completedLocalDate)
                .participantCount(instance.getParticipantCount())
                .pointPerPerson(instance.getPointPerPerson())
                .description(instance.getDescription())
                .notice(instance.getNotice())
                .certificationMethod(instance.getCertificationMethod())
                .joinStatus(joinStatus)
                .hitCount(instance.getHitsList().size())
                .fileResponse(FileResponse.create(instance.getFiles()))
                .build();
    }
}
