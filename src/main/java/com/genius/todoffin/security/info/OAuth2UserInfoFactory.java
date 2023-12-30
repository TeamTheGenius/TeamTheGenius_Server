package com.genius.todoffin.security.info;

import com.genius.todoffin.security.constants.ProviderType;
import com.genius.todoffin.security.info.impl.FacebookOAuth2UserInfo;
import com.genius.todoffin.security.info.impl.GoogleOAuth2UserInfo;
import com.genius.todoffin.security.info.impl.KakaoOAuth2UserInfo;
import com.genius.todoffin.security.info.impl.NaverOAuth2UserInfo;
import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(ProviderType.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        if (registrationId.equalsIgnoreCase(ProviderType.NAVER.toString())) {
            return new NaverOAuth2UserInfo(attributes);
        }
        if (registrationId.equalsIgnoreCase(ProviderType.KAKAO.toString())) {
            return new KakaoOAuth2UserInfo(attributes);
        }
        if (registrationId.equalsIgnoreCase(ProviderType.FACEBOOK.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        }

        throw new OAuth2AuthenticationException("Unsupported Login Type : " + registrationId);
    }
}
