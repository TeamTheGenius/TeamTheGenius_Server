package com.genius.gitget.global.security.info;

import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.security.info.impl.GithubOAuth2UserInfo;
import com.genius.gitget.global.security.info.impl.GoogleOAuth2UserInfo;
import com.genius.gitget.global.security.info.impl.KakaoOAuth2UserInfo;
import com.genius.gitget.global.security.info.impl.NaverOAuth2UserInfo;
import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderInfo providerInfo, Map<String, Object> attributes) {
        switch (providerInfo) {
            case GITHUB -> {
                return new GithubOAuth2UserInfo(attributes);
            }
            case KAKAO -> {
                return new KakaoOAuth2UserInfo(attributes);
            }
            case NAVER -> {
                return new NaverOAuth2UserInfo(attributes);
            }
            case GOOGLE -> {
                return new GoogleOAuth2UserInfo(attributes);
            }
        }
        throw new OAuth2AuthenticationException("INVALID PROVIDER TYPE");
    }
}
