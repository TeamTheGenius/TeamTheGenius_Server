package com.genius.gitget.challenge.certification.dto;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record CertificationResponse(
        Long certificationId,
        LocalDate certificatedAt,
        CertificateStatus certificateStatus,
        int prCount,
        List<String> prLinks
) {

    public static CertificationResponse create(Certification certification) {
        List<String> prLinks = getPrList(certification.getCertificationLinks());

        return CertificationResponse.builder()
                .certificationId(certification.getId())
                .certificatedAt(certification.getCertificatedAt())
                .certificateStatus(certification.getCertificationStatus())
                .prLinks(prLinks)
                .prCount(prLinks.size())
                .build();
    }

    private static List<String> getPrList(String prLink) {
        List<String> prLinkList = List.of(prLink.split(","));
        if (prLinkList.size() == 1 && prLinkList.get(0).isEmpty()) {
            return new ArrayList<>();
        }
        return prLinkList;
    }
}
