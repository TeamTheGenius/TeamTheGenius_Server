package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.dto.detail.InstanceResponse;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.dto.detail.JoinResponse;
import com.genius.gitget.challenge.user.domain.User;

public interface InstanceDetailFacade {
    InstanceResponse getInstanceDetailInformation(User user, Long instanceId);

    JoinResponse joinNewChallenge(User user, JoinRequest joinRequest);

    public JoinResponse quitChallenge(User user, Long instanceId);
}
