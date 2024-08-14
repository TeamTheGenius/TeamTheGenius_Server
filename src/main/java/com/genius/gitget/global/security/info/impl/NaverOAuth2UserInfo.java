package com.genius.gitget.global.security.info.impl;


import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.security.info.OAuth2UserInfo;
import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get(ProviderInfo.NAVER.getAttributeKey()));
    }

    @Override
    public String getProviderCode() {
        return (String) attributes.get(ProviderInfo.NAVER.getProviderCode());
    }

    @Override
    public String getUserIdentifier() {
        return (String) attributes.get(ProviderInfo.NAVER.getIdentifier());
    }
}
