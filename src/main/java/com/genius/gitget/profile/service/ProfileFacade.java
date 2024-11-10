package com.genius.gitget.profile.service;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.profile.dto.UserChallengeResultResponse;
import com.genius.gitget.profile.dto.UserDetailsInformationResponse;
import com.genius.gitget.profile.dto.UserInformationResponse;
import com.genius.gitget.profile.dto.UserInformationUpdateRequest;
import com.genius.gitget.profile.dto.UserInterestResponse;
import com.genius.gitget.profile.dto.UserInterestUpdateRequest;
import com.genius.gitget.profile.dto.UserPointResponse;

public interface ProfileFacade {
    public UserPointResponse getUserPoint(User user);

    public UserInformationResponse getUserInformation(Long userId);

    public UserDetailsInformationResponse getUserDetailsInformation(User user);

    public Long updateUserInformation(User user, UserInformationUpdateRequest userInformationUpdateRequest);

    public void deleteUserInformation(User user, String reason);

    public void updateUserTags(User user, UserInterestUpdateRequest userInterestUpdateRequest);

    public UserInterestResponse getUserInterest(User user);

    public UserChallengeResultResponse getUserChallengeResult(User user);

}
