package com.genius.todoffin.security;

import com.genius.todoffin.user.dto.OAuthAttributes;
import com.genius.todoffin.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // RegistrationId() : 서비스를 구분하는 코드
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 진행 시 키가 되는 필드값을 이야기 한다. Primary Key와 같은 의미.
        // 구글의 경우 기본적으로 코드를 지원하지만, 네이버 카카 등은 기본 지원하지 않는다. 구굴의 기본 코드는 "sub"
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuthAttributes
        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담은 클래스.
        // 이후 네이버 등 다른 소셜 로그인도 이 클래스를 사용
        OAuthAttributes authAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());


        String email = "";

        switch (registrationId) {
            case "kakao":
                //

            case "facebook" :
                //

            case "naver" :
                //

            case "google " :
                //
            default:
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
        }

        // 유저가 존재하거나, 새로운 유저일 경우 비즈니스 로직 /



//        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey()))
//                ,oAuth2User.getAttributes()
//                ,userNameAttributeName);
    }
}
