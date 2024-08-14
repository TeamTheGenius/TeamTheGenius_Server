package com.genius.gitget.challenge.myChallenge.facade;

import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.user.domain.User;
import java.time.LocalDate;
import java.util.List;

public interface MyChallengeFacade {
    List<PreActivityResponse> getPreActivityInstances(User user, LocalDate targetDate);

    List<ActivatedResponse> getActivatedInstances(User user, LocalDate targetDate);

    List<DoneResponse> getDoneInstances(User user, LocalDate targetDate);

    DoneResponse getRewards(RewardRequest rewardRequest);
}
