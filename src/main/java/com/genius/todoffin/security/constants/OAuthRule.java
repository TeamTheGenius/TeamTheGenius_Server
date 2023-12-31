package com.genius.todoffin.security.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthRule {
    KAKAO_PROVIDER_ID("id"),
    NAVER_PROVIDER_ID("id"),
    GOOGLE_PROVIDER_ID("sub"),
    FACEBOOK_PROVIDER_ID("id"),
    EMAIL_KEY("email");

    private final String value;
}
