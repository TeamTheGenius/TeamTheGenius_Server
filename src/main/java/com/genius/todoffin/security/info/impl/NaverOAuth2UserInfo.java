package com.genius.todoffin.security.info.impl;


import static com.genius.todoffin.security.constants.OAuthRule.COMMON_USER_KEY;
import static com.genius.todoffin.security.constants.OAuthRule.NAVER_PROVIDER_ID;

import com.genius.todoffin.security.info.OAuth2UserInfo;
import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {
    private final static String ATTRIBUTE_KEY = "response";

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get(ATTRIBUTE_KEY));
    }

    @Override
    public String getProviderCode() {
        return (String) attributes.get(NAVER_PROVIDER_ID.getValue());
    }

    @Override
    public String getUserIdentifier() {
        return (String) attributes.get(COMMON_USER_KEY.getValue());
    }
}
