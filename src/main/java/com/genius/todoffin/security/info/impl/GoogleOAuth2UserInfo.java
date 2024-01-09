package com.genius.todoffin.security.info.impl;


import static com.genius.todoffin.security.constants.OAuthRule.COMMON_USER_KEY;
import static com.genius.todoffin.security.constants.OAuthRule.GOOGLE_PROVIDER_ID;

import com.genius.todoffin.security.info.OAuth2UserInfo;
import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderCode() {
        return (String) attributes.get(GOOGLE_PROVIDER_ID.getValue());
    }

    @Override
    public String getUserIdentifier() {
        return (String) attributes.get(COMMON_USER_KEY.getValue());
    }
}
