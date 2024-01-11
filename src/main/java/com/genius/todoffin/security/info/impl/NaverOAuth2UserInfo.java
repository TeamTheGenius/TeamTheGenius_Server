package com.genius.todoffin.security.info.impl;


import static com.genius.todoffin.security.constants.ProviderInfo.NAVER;

import com.genius.todoffin.security.info.OAuth2UserInfo;
import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get(NAVER.getAttributeKey()));
    }

    @Override
    public String getProviderCode() {
        return (String) attributes.get(NAVER.getProviderCode());
    }

    @Override
    public String getUserIdentifier() {
        return (String) attributes.get(NAVER.getIdentifier());
    }
}
