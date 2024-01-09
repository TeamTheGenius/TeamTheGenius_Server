package com.genius.todoffin.security.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthRule {
    COMMON_USER_KEY("email"),

    GITHUB_PROVIDER_ID("id"),
    GITHUB_USER_IDENTIFIER("login"),

    KAKAO_PROVIDER_ID("id"),
    NAVER_PROVIDER_ID("id"),
    GOOGLE_PROVIDER_ID("sub");

    private final String value;
}
