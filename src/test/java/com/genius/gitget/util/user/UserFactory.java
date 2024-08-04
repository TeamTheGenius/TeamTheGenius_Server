package com.genius.gitget.util.user;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.security.constants.ProviderInfo;

public class UserFactory {

    public static User createUser() {
        return User.builder()
                .role(Role.USER)
                .nickname("nickname")
                .providerInfo(ProviderInfo.GITHUB)
                .identifier("githubId")
                .information("information")
                .tags("BE,FE")
                .build();
    }

    public static User createAdmin() {
        return User.builder()
                .role(Role.ADMIN)
                .nickname("nickname")
                .providerInfo(ProviderInfo.GITHUB)
                .identifier("githubId")
                .information("information")
                .tags("BE,FE")
                .build();
    }
}
