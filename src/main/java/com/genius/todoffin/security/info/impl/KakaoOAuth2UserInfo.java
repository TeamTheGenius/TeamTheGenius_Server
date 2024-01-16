package com.genius.todoffin.security.info.impl;

import static com.genius.todoffin.security.constants.ProviderInfo.KAKAO;

import com.genius.todoffin.security.info.OAuth2UserInfo;
import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    private String providerId;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get(KAKAO.getAttributeKey()));
        this.providerId = String.valueOf(attributes.get(KAKAO.getIdentifier()));
    }

    @Override
    public String getProviderCode() {
        return providerId;
    }

    @Override
    public String getUserIdentifier() {
        return (String) attributes.get(KAKAO.getProviderCode());
    }
}
