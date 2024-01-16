package com.genius.todoffin.security.handler;

import static com.genius.todoffin.security.constants.OAuthRule.EMAIL_KEY;

import com.genius.todoffin.user.domain.Role;
import com.genius.todoffin.user.domain.User;
import com.genius.todoffin.user.repository.UserRepository;
import com.genius.todoffin.util.exception.BusinessException;
import com.genius.todoffin.util.exception.ErrorCode;
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
        String identifier = oAuth2User.getName();

        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Role role = user.getRole();

        String redirectUrl = getRedirectUrlByRole(role, identifier);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String getRedirectUrlByRole(Role role, String email) {
        if (role == Role.NOT_REGISTERED) {
            return UriComponentsBuilder.fromUriString(SIGNUP_URL)
                    .queryParam(EMAIL_KEY.getValue(), email)
                    .build()
                    .toUriString();
        }

        return UriComponentsBuilder.fromHttpUrl(MAIN_URL)
                .build()
                .toUriString();
    }
}
