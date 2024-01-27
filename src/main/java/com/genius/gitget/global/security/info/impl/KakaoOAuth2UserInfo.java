package com.genius.gitget.global.security.info.impl;

import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.security.info.OAuth2UserInfo;
import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    private String providerId;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get(ProviderInfo.KAKAO.getAttributeKey()));
        this.providerId = String.valueOf(attributes.get(ProviderInfo.KAKAO.getIdentifier()));
    }

    @Override
    public String getProviderCode() {
        return providerId;
    }

    @Override
    public String getUserIdentifier() {
        return (String) attributes.get(ProviderInfo.KAKAO.getProviderCode());
    }
}
