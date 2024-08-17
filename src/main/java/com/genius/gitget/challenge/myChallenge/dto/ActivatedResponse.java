package com.genius.gitget.challenge.myChallenge.dto;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.store.item.dto.OrderResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivatedResponse extends OrderResponse {
    private Long instanceId;
    private String title;
    private int pointPerPerson;
    private String repository;
    private String certificateStatus;
    private int numOfPassItem;
    private boolean canUsePassItem;
    private FileResponse fileResponse;

    @Builder
    public ActivatedResponse(Long instanceId, String title, int pointPerPerson, String repository,
                             String certificateStatus,
                             int numOfPassItem, boolean canUsePassItem, FileResponse fileResponse) {
        this.instanceId = instanceId;
        this.title = title;
        this.pointPerPerson = pointPerPerson;
        this.repository = repository;
        this.certificateStatus = certificateStatus;
        this.numOfPassItem = numOfPassItem;
        this.canUsePassItem = canUsePassItem;
        this.fileResponse = fileResponse;
    }

    public static ActivatedResponse of(Instance instance, CertificateStatus certificateStatus,
                                       int numOfPassItem, String repository, FileResponse fileResponse) {
        boolean canUseItem = checkItemCondition(certificateStatus, numOfPassItem);

        return ActivatedResponse.builder()
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .pointPerPerson(instance.getPointPerPerson())
                .repository(repository)
                .certificateStatus(certificateStatus.getTag())
                .canUsePassItem(canUseItem)
                .numOfPassItem(canUseItem ? numOfPassItem : 0)
                .fileResponse(fileResponse)
                .build();
    }

    private static boolean checkItemCondition(CertificateStatus certificateStatus, int numOfPassItem) {
        return (certificateStatus == CertificateStatus.NOT_YET) && (numOfPassItem > 0);
    }
}
