package com.genius.todoffin.security.handler;

import com.genius.todoffin.user.domain.Role;
import com.genius.todoffin.user.domain.User;
import com.genius.todoffin.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final String SIGNUP_URL = "http://localhost:5173/login/signup";
    private final String MAIN_URL = "http://localhost:5173/main";
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getName();

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
