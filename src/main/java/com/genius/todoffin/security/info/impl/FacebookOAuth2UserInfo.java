package com.genius.todoffin.security.info.impl;

import static com.genius.todoffin.security.constants.OAuthRule.EMAIL_KEY;
import static com.genius.todoffin.security.constants.OAuthRule.FACEBOOK_PROVIDER_ID;

import com.genius.todoffin.security.info.OAuth2UserInfo;
import java.util.Map;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {

    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get(FACEBOOK_PROVIDER_ID.getValue());
    }

    @Override
    public String getEmail() {
        return (String) attributes.get(EMAIL_KEY.getValue());
    }
}

