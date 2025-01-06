package com.genius.gitget.util.user;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.security.constants.ProviderInfo;

public class UserFactory {

    public static User createByInfo(String identifier, Role role) {
        return User.builder()
                .role(role)
                .providerInfo(ProviderInfo.GITHUB)
                .identifier(identifier)
                .information("information")
                .tags("BE,FE")
                .build();
    }

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

    public static User createUnregistered(String identifier) {
        return User.builder()
                .identifier(identifier)
                .role(Role.NOT_REGISTERED)
                .providerInfo(ProviderInfo.GITHUB)
                .build();
    }
}
