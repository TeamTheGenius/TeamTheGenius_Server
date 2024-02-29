package com.genius.gitget.challenge.certification.dto;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record CertificationResponse(
        Long certificationId,
        int certificationAttempt,
        DayOfWeek dayOfWeek,
        LocalDate certificatedAt,
        CertificateStatus certificateStatus,
        int prCount,
        List<String> prLinks
) {

    public static CertificationResponse createFail(int currentAttempt) {
        return CertificationResponse.builder()
                .certificationId(0L)
                .certificationAttempt(currentAttempt)
                .dayOfWeek(null)
                .certificatedAt(null)
                .certificateStatus(null)
                .prLinks(null)
                .prCount(0)
                .build();
    }

    public static CertificationResponse createSuccess(Certification certification) {
        List<String> prLinks = getPrList(certification.getCertificationLinks());

        return CertificationResponse.builder()
                .certificationId(certification.getId())
                .certificationAttempt(certification.getCurrentAttempt())
                .dayOfWeek(certification.getCertificatedAt().getDayOfWeek())
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
