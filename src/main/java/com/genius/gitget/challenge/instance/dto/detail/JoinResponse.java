package com.genius.gitget.challenge.instance.dto.detail;

import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import lombok.Builder;

@Builder
public record JoinResponse(
        Long participantId,
        JoinStatus joinStatus,
        JoinResult joinResult
) {
    public static JoinResponse createJoinResponse(Participant participant) {
        return JoinResponse.builder()
                .participantId(participant.getId())
                .joinStatus(participant.getJoinStatus())
                .joinResult(participant.getJoinResult())
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
