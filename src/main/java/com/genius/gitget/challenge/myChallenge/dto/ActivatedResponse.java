package com.genius.gitget.challenge.myChallenge.dto;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;

@Builder
public record ActivatedResponse(
        Long instanceId,
        String title,
        int pointPerPerson,
        String repository,
        String certificateStatus,
        int numOfPassItem,
        boolean canUsePassItem,
        FileResponse fileResponse
) {

    public static ActivatedResponse create(Instance instance, CertificateStatus certificateStatus,
                                           int numOfPassItem, String repository) {
        boolean canUseItem = checkItemCondition(certificateStatus, numOfPassItem);

        return ActivatedResponse.builder()
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .pointPerPerson(instance.getPointPerPerson())
                .repository(repository)
                .certificateStatus(certificateStatus.getTag())
                .canUsePassItem(canUseItem)
                .numOfPassItem(canUseItem ? numOfPassItem : 0)
                .fileResponse(FileResponse.create(instance.getFiles()))
                .build();
    }

    private static boolean checkItemCondition(CertificateStatus certificateStatus, int numOfPassItem) {
        return (certificateStatus == CertificateStatus.NOT_YET) && (numOfPassItem > 0);
    }
}
