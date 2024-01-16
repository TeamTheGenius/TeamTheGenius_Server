package com.genius.gitget.security.info.impl;


import static com.genius.gitget.security.constants.ProviderInfo.GOOGLE;

import com.genius.gitget.security.info.OAuth2UserInfo;
import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderCode() {
        return (String) attributes.get(GOOGLE.getProviderCode());
    }

    @Override
    public String getUserIdentifier() {
        return (String) attributes.get(GOOGLE.getIdentifier());
    }
}
