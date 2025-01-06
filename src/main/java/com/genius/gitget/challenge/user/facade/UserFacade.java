package com.genius.gitget.challenge.user.facade;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.dto.LoginRequest;
import com.genius.gitget.challenge.user.dto.SignupRequest;
import com.genius.gitget.global.security.dto.AuthResponse;
import com.genius.gitget.global.security.dto.SignupResponse;

public interface UserFacade {
    void isNicknameDuplicate(String nickname);

    SignupResponse signup(SignupRequest signupRequest);

    AuthResponse getUserAuthInfo(String identifier);

    User getAuthUser(String identifier);

    User getGuestUser(LoginRequest loginRequest);
}
