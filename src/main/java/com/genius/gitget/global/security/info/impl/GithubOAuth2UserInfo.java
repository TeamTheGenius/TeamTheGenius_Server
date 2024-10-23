package com.genius.gitget.global.security.info.impl;

import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.security.info.OAuth2UserInfo;
import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderCode() {
        return (String) attributes.get(ProviderInfo.GITHUB.getProviderCode());
    }

    @Override
    public String getUserIdentifier() {
        return (String) attributes.get(ProviderInfo.GITHUB.getIdentifier());
    }
}
