package com.genius.todoffin.security.service;

import com.genius.todoffin.security.constants.ProviderType;
import com.genius.todoffin.security.domain.UserPrincipal;
import com.genius.todoffin.security.info.OAuth2UserInfo;
import com.genius.todoffin.security.info.OAuth2UserInfoFactory;
import com.genius.todoffin.user.entity.Role;
import com.genius.todoffin.user.entity.User;
import com.genius.todoffin.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
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

        // OAuth2 로그인 진행 시 키가 되는 필드값. Primary Key와 같은 의미.
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 서비스를 구분하는 코드
        String providerId = userRequest.getClientRegistration().getRegistrationId();

        ProviderType providerType = ProviderType.from(providerId);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, attributes);
        String email = oAuth2UserInfo.getEmail();

        User user = getUser(email, providerType);

        return new UserPrincipal(user, attributes, userNameAttributeName);
    }

    private User getUser(String email, ProviderType providerType) {
        Optional<User> optionalUser = userRepository.findByOAuthInfo(email, providerType);

        if (optionalUser.isEmpty()) {
            User unregisteredUser = User.builder()
                    .email(email)
                    .role(Role.NOT_REGISTERED)
                    .provider(providerType)
                    .build();
            return userRepository.save(unregisteredUser);
        }
        return optionalUser.get();
    }
}
