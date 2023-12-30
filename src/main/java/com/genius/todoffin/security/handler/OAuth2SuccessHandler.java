package com.genius.todoffin.security.handler;

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
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");

        //TODO: 추후 Utils의 ErrorCode를 활용하여 orElseThrow에 에러 코드 넣기
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Role role = user.getRole();

        if (role == Role.NOT_REGISTERED) {
            //TODO: url에 대해 상수로 빼기
            String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login/signup")
                    .build()
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            return;
        }

        String redirectUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:3000/main")
                .build()
                .toUriString();
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
