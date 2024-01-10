package com.genius.todoffin.security.service;

import com.genius.todoffin.user.domain.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final JwtGenerator jwtGenerator;

    @Value("${jwt.access-secret}")
    private String ACCESS_SECRET;
    @Value("${jwt.refresh-secret}")
    private String REFRESH_SECRET;
    @Value("${jwt.access-expiration}")
    private long ACCESS_EXPIRATION;
    @Value("${jwt.refresh-expiration}")
    private long REFRESH_EXPIRATION;


    public void generateAccessToken(HttpServletResponse response, User requestUser) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET, ACCESS_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie("access-token", accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void generateRefreshToken(HttpServletResponse response) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET, REFRESH_EXPIRATION);
        ResponseCookie cookie = setTokenToCookie("refresh-token", refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private ResponseCookie setTokenToCookie(String tokenType, String token, long maxAgeSeconds) {
        return ResponseCookie.from(tokenType, token)
                .path("/")
                .maxAge(maxAgeSeconds)
                .httpOnly(true)
                .secure(true)
                .build();
    }
}
