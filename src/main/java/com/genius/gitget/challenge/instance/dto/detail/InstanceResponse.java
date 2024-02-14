package com.genius.gitget.challenge.instance.dto.detail;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.Builder;

@Builder
public record InstanceResponse(
        Long instanceId,
        int remainDays,
        String period,
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
                .instanceId(instance.getId())
                .remainDays(getRemainDays(startedLocalDate))
                .period(startedLocalDate + " ~ " + completedLocalDate)
                .participantCount(instance.getParticipantCount())
                .pointPerPerson(instance.getPointPerPerson())
                .description(instance.getDescription())
                .notice(instance.getNotice())
                .certificationMethod(instance.getCertificationMethod())
                .joinStatus(joinStatus)
                .hitCount(instance.getHitsList().size())
                .fileResponse(convertToFileResponse(instance.getFiles()))
                .build();
    }

    private static int getRemainDays(LocalDate startedDate) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(startedDate)) {
            return (int) ChronoUnit.DAYS.between(now, startedDate);
        }
        return 0;
    }

    private static FileResponse convertToFileResponse(Optional<Files> optionalFiles) {
        if (optionalFiles.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(optionalFiles.get());
    }
}
