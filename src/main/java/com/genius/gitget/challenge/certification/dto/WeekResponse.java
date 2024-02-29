package com.genius.gitget.challenge.certification.dto;

import com.genius.gitget.challenge.user.domain.User;
import java.util.List;
import lombok.Builder;

@Builder
public record WeekResponse(
        Long userId,
        String nickname,
        List<CertificationResponse> certificationResponses
) {

    public static WeekResponse create(User user, List<CertificationResponse> certificationResponses) {
        return WeekResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .certificationResponses(certificationResponses)
                .build();
    }
}
