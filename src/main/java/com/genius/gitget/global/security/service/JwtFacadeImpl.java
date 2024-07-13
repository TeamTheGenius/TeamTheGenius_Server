package com.genius.gitget.global.security.service;

import static com.genius.gitget.global.security.constants.JwtRule.ACCESS_HEADER;
import static com.genius.gitget.global.security.constants.JwtRule.ACCESS_PREFIX;
import static com.genius.gitget.global.security.constants.JwtRule.REFRESH_ISSUE;
import static com.genius.gitget.global.security.constants.JwtRule.REFRESH_PREFIX;
import static com.genius.gitget.global.util.exception.ErrorCode.JWT_NOT_FOUND_IN_COOKIE;
import static com.genius.gitget.global.util.exception.ErrorCode.JWT_NOT_FOUND_IN_HEADER;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.security.constants.TokenStatus;
import com.genius.gitget.global.security.domain.Token;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class JwtFacadeImpl implements JwtFacade {
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenService tokenService;
    private final JwtGenerator jwtGenerator;
    private final JwtUtil jwtUtil;

    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;

    public JwtFacadeImpl(CustomUserDetailsService customUserDetailsService,
                         TokenService tokenService,
                         JwtGenerator jwtGenerator, JwtUtil jwtUtil,
                         @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
                         @Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
                         @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
                         @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION) {
        this.customUserDetailsService = customUserDetailsService;
        this.tokenService = tokenService;
        this.jwtGenerator = jwtGenerator;
        this.jwtUtil = jwtUtil;
        this.ACCESS_SECRET_KEY = jwtUtil.getSigningKey(ACCESS_SECRET_KEY);
        this.REFRESH_SECRET_KEY = jwtUtil.getSigningKey(REFRESH_SECRET_KEY);
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
    }


    @Override
    public String generateAccessToken(HttpServletResponse response, User requestUser) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, requestUser);
        String bearer = ACCESS_PREFIX.getValue() + accessToken;
        response.setHeader(ACCESS_HEADER.getValue(), bearer);

        return accessToken;
    }

    @Override
    @Transactional
    public String generateRefreshToken(HttpServletResponse response, User requestUser) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader(REFRESH_ISSUE.getValue(), cookie.toString());

        tokenService.save(new Token(requestUser.getIdentifier(), refreshToken));
        return refreshToken;
    }

    private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAgeSeconds) {
        return ResponseCookie.from(tokenPrefix, token)
                .path("/")
                .maxAge(maxAgeSeconds)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)
                .build();
    }

    @Override
    public boolean validateAccessToken(String token) {
        return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY) == TokenStatus.AUTHENTICATED;
    }

    @Override
    public boolean validateRefreshToken(String token, String identifier) {
        boolean isRefreshValid = jwtUtil.getTokenStatus(token, REFRESH_SECRET_KEY) == TokenStatus.AUTHENTICATED;
        boolean isHijacked = tokenService.isRefreshHijacked(identifier, token);

        return isRefreshValid && !isHijacked;
    }

    @Override
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerHeader = request.getHeader(ACCESS_HEADER.getValue());
        if (bearerHeader == null || bearerHeader.isEmpty()) {
            throw new BusinessException(JWT_NOT_FOUND_IN_HEADER);
        }
        return bearerHeader.trim().substring(7);
    }

    @Override
    public String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new BusinessException(JWT_NOT_FOUND_IN_COOKIE);
        }
        return jwtUtil.resolveTokenFromCookie(cookies, REFRESH_PREFIX);
    }

    @Override
    public String getIdentifierFromRefresh(String refreshToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(REFRESH_SECRET_KEY)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_JWT);
        }
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        UserDetails principal = customUserDetailsService.loadUserByUsername(getUserPk(accessToken, ACCESS_SECRET_KEY));
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    private String getUserPk(String token, Key secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public void logout(HttpServletResponse response, String identifier) {
        tokenService.deleteById(identifier);

        Cookie refreshCookie = jwtUtil.resetCookie(REFRESH_PREFIX);
        response.addCookie(refreshCookie);
    }
}
