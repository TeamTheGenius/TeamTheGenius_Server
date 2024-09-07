package com.genius.gitget.challenge.certification.dto;

import com.genius.gitget.challenge.user.dto.UserProfileInfo;
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
    public static WeekResponse create(UserProfileInfo userProfileInfo,
                                      List<CertificationResponse> certifications) {
        return WeekResponse.builder()
                .userId(userProfileInfo.userId())
                .nickname(userProfileInfo.nickname())
                .frameId(userProfileInfo.frameId())
                .certifications(certifications)
                .profile(userProfileInfo.fileResponse())
                .build();
    }
}
