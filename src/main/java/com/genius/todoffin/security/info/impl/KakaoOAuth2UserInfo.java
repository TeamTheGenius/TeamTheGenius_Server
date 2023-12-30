package com.genius.todoffin.security.info.impl;

import com.genius.todoffin.security.info.OAuth2UserInfo;
import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    private final static String ATTRIBUTE_KEY = "kakao_account";

    private Long id;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get(ATTRIBUTE_KEY));
        this.id = (Long) attributes.get("id");
    }

    @Override
    public String getId() {
        return this.id.toString();
    }

    @Override
    public String getName() {
        return (String) ((Map<String, Object>) attributes.get("profile")).get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
