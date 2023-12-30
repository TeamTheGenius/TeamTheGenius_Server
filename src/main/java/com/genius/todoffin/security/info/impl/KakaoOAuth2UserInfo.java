package com.genius.todoffin.security.info.impl;

import static com.genius.todoffin.security.constants.OAuthAttributeKey.EMAIL_KEY;
import static com.genius.todoffin.security.constants.OAuthAttributeKey.KAKAO_PROVIDER_ID;

import com.genius.todoffin.security.info.OAuth2UserInfo;
import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    private final static String ATTRIBUTE_KEY = "kakao_account";
    private String providerId;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get(ATTRIBUTE_KEY));
        this.providerId = String.valueOf(attributes.get(KAKAO_PROVIDER_ID.getValue()));
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public String getEmail() {
        return (String) attributes.get(EMAIL_KEY.getValue());
    }
}
