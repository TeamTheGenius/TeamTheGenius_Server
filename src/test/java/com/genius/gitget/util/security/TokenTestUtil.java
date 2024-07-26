package com.genius.gitget.util.security;

import static com.genius.gitget.global.security.constants.JwtRule.ACCESS_PREFIX;
import static com.genius.gitget.global.security.constants.JwtRule.REFRESH_PREFIX;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.security.constants.JwtRule;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.security.service.JwtFacadeService;
import jakarta.servlet.http.Cookie;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class TokenTestUtil {
    private final JwtFacadeService jwtFacade;

    public Cookie createAccessHeader() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        String accessCookie = jwtFacade.generateAccessToken(httpServletResponse, user);
        return new Cookie(ACCESS_PREFIX.getValue(), accessCookie);
    }

    public HttpHeaders createAccessHeaders() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtFacade.generateAccessToken(httpServletResponse, user);
        String bearerAccess = ACCESS_PREFIX.getValue() + accessToken;

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(JwtRule.ACCESS_HEADER.getValue(), Collections.singletonList(bearerAccess));
        return HttpHeaders.readOnlyHttpHeaders(headers);
    }

    public String createAccessToken() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        return jwtFacade.generateAccessToken(httpServletResponse, user);
    }

    public Cookie createRefreshCookie() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        String refreshCookie = jwtFacade.generateRefreshToken(httpServletResponse, user);
        return new Cookie(REFRESH_PREFIX.getValue(), refreshCookie);
    }

    public String createRefreshToken() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userPrincipal.getUser();

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        return jwtFacade.generateRefreshToken(httpServletResponse, user);
    }
}
