package com.genius.gitget.security.info.impl;

import static com.genius.gitget.security.constants.ProviderInfo.GITHUB;

import com.genius.gitget.security.info.OAuth2UserInfo;
import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderCode() {
        return (String) attributes.get(GITHUB.getProviderCode());
    }

    @Override
    public String getUserIdentifier() {
        return (String) attributes.get(GITHUB.getIdentifier());
    }
}
