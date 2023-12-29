package com.genius.security.constants;

import java.util.Arrays;

public enum AuthProvider {
    KAKAO,
    NAVER,
    GOOGLE,
    FACEBOOK;

    public static AuthProvider from(String provider) {
        return Arrays.stream(AuthProvider.values())
                .filter(item -> item.name().equals(provider))
                .findFirst()
                .orElseThrow();
    }
}
