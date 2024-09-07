package com.genius.gitget.challenge.instance.dto.crud;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InstanceUpdateDTO(
        String description,
        String notice,
        int pointPerPerson,
        LocalDateTime startedDate,

        LocalDateTime completedDate,
        String certificationMethod) {
    public static InstanceUpdateDTO of(String description, String notice, int pointPerPerson, LocalDateTime startedDate,
                                       LocalDateTime completedDate, String certificationMethod) {
        return InstanceUpdateDTO.builder()
                .description(description)
                .notice(notice)
                .pointPerPerson(pointPerPerson)
                .startedDate(startedDate)
                .completedDate(completedDate)
                .certificationMethod(certificationMethod)
                .build();
    }
}
