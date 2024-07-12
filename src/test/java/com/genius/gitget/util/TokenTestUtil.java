package com.genius.gitget.util;

import static com.genius.gitget.global.security.constants.JwtRule.ACCESS_HEADER_PREFIX;
import static com.genius.gitget.global.security.constants.JwtRule.REFRESH_PREFIX;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.security.service.JwtFacadeImpl;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenTestUtil {
    private final JwtFacadeImpl jwtService;

    public Cookie createAccessCookie() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        String accessCookie = jwtService.generateAccessToken(httpServletResponse, user);
        return new Cookie(ACCESS_HEADER_PREFIX.getValue(), accessCookie);
    }

    public String createAccessToken() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        return jwtService.generateAccessToken(httpServletResponse, user);
    }

    public Cookie createRefreshCookie() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        String refreshCookie = jwtService.generateRefreshToken(httpServletResponse, user);
        return new Cookie(REFRESH_PREFIX.getValue(), refreshCookie);
    }

    public String createRefreshToken() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        return jwtService.generateRefreshToken(httpServletResponse, user);
    }
}
