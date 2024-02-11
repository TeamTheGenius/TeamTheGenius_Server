package com.genius.gitget.challenge.instance.dto.detail;

import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import lombok.Builder;

@Builder
public record JoinResponse(
        Long participantId,
        JoinStatus joinStatus,
        JoinResult joinResult
) {
    public static JoinResponse createJoinResponse(ParticipantInfo participantInfo) {
        return JoinResponse.builder()
                .participantId(participantInfo.getId())
                .joinStatus(participantInfo.getJoinStatus())
                .joinResult(participantInfo.getJoinResult())
                .build();
    }

    public static JoinResponse createQuitResponse() {
        return JoinResponse.builder()
                .participantId(null)
                .joinResult(null)
                .joinStatus(null)
                .build();
    }
}
