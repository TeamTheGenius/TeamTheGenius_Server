package com.genius.todoffin.security.constants;

import java.util.Arrays;

public enum ProviderType {
    GITHUB,
    KAKAO,
    NAVER,
    GOOGLE,
    FACEBOOK;

    public static ProviderType from(String provider) {
        String upperCastedProvider = provider.toUpperCase();

        return Arrays.stream(ProviderType.values())
                .filter(item -> item.name().equals(upperCastedProvider))
                .findFirst()
                .orElseThrow();
    }
}
