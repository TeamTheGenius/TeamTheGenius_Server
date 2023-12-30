package com.genius.todoffin.security.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthAttributeKey {
    KAKAO_PROVIDER_ID("id"),
    NAVER_PROVIDER_ID("id"),
    GOOGLE_PROVIDER_ID("sub"),
    EMAIL_KEY("email");

    private final String value;
}
