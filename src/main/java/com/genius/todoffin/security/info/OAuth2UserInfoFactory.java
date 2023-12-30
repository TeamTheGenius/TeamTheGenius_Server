package com.genius.todoffin.security.info;

import com.genius.todoffin.security.constants.ProviderType;
import com.genius.todoffin.security.info.impl.FacebookOAuth2UserInfo;
import com.genius.todoffin.security.info.impl.GoogleOAuth2UserInfo;
import com.genius.todoffin.security.info.impl.KakaoOAuth2UserInfo;
import com.genius.todoffin.security.info.impl.NaverOAuth2UserInfo;
import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case KAKAO -> {
                return new KakaoOAuth2UserInfo(attributes);
            }
            case NAVER -> {
                return new NaverOAuth2UserInfo(attributes);
            }
            case GOOGLE -> {
                return new GoogleOAuth2UserInfo(attributes);
            }
            case FACEBOOK -> {
                return new FacebookOAuth2UserInfo(attributes);
            }
        }
        throw new OAuth2AuthenticationException("INVALID PROVIDER TYPE");
    }
}
