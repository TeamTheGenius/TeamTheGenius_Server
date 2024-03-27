package com.genius.gitget.challenge.certification.dto;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record WeekResponse(
        Long userId,
        String nickname,
        Long frameId,
        List<CertificationResponse> certifications,
        FileResponse profile

) {

    public static WeekResponse create(User user, FileResponse fileResponse,
                                      List<CertificationResponse> certifications) {
        return WeekResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .certifications(certifications)
                .profile(fileResponse)
                .build();
    }

    public static WeekResponse create(User user, Long frameId, FileResponse fileResponse,
                                      List<CertificationResponse> certifications) {
        return WeekResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .frameId(frameId)
                .certifications(certifications)
                .profile(fileResponse)
                .build();
    }
}
