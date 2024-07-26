package com.genius.gitget.util.participant;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.user.domain.User;

public class ParticipantFactory {
    public static Participant create(User user, Instance instance) {
        Participant participant = Participant.builder()
                .joinResult(JoinResult.PROCESSING)
                .joinStatus(JoinStatus.YES)
                .build();
        participant.setUserAndInstance(user, instance);
        participant.updateRepository("targetRepo");

        return participant;
    }
}
