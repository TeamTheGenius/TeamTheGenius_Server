package com.genius.todoffin.security.handler;

import static com.genius.todoffin.security.constants.OAuthAttributeKey.EMAIL_KEY;

import com.genius.todoffin.user.entity.Role;
import com.genius.todoffin.user.entity.User;
import com.genius.todoffin.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final String SIGNUP_URL = "http://localhost:3000/login/signup";
    private final String MAIN_URL = "http://localhost:3000/main";
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get(EMAIL_KEY.getValue());

        //TODO: 추후 Utils의 ErrorCode를 활용하여 orElseThrow에 에러 코드 넣기
        User user = userRepository.findByEmail(email)
                .orElseThrow();
        Role role = user.getRole();

        String redirectUrl = getRedirectUrlByRole(role);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String getRedirectUrlByRole(Role role) {
        if (role == Role.NOT_REGISTERED) {
            return UriComponentsBuilder.fromUriString(SIGNUP_URL)
                    .build()
                    .toUriString();
        }

        return UriComponentsBuilder.fromHttpUrl(MAIN_URL)
                .build()
                .toUriString();
    }
}
