package com.genius.todoffin.security.service;

import static com.genius.todoffin.security.constants.JwtRule.ACCESS_PREFIX;
import static com.genius.todoffin.security.constants.JwtRule.JWT_ISSUE_HEADER;
import static com.genius.todoffin.security.constants.JwtRule.REFRESH_PREFIX;

import com.genius.todoffin.security.constants.JwtRule;
import com.genius.todoffin.security.domain.Token;
import com.genius.todoffin.security.repository.TokenRepository;
import com.genius.todoffin.user.domain.User;
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
public class JwtService {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtGenerator jwtGenerator;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;

    public JwtService(CustomUserDetailsService customUserDetailsService, JwtGenerator jwtGenerator,
                      JwtUtil jwtUtil, TokenRepository tokenRepository,
                      @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
                      @Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
                      @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
                      @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtGenerator = jwtGenerator;
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.ACCESS_SECRET_KEY = jwtUtil.getSigningKey(ACCESS_SECRET_KEY);
        this.REFRESH_SECRET_KEY = jwtUtil.getSigningKey(REFRESH_SECRET_KEY);
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
    }

    public void generateAccessToken(HttpServletResponse response, User requestUser) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());
    }

    @Transactional
    public void generateRefreshToken(HttpServletResponse response, User requestUser) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());

        tokenRepository.save(new Token(requestUser.getIdentifier(), refreshToken));
    }

    private ResponseCookie setTokenToCookie(String tokenType, String token, long maxAgeSeconds) {
        return ResponseCookie.from(tokenType, token)
                .path("/")
                .maxAge(maxAgeSeconds)
                .httpOnly(true)
                .secure(true)
                .build();
    }

    public boolean validateAccessToken(String token) {
        return jwtUtil.validateToken(token, ACCESS_SECRET_KEY);
    }

    public boolean validateRefreshToken(String token, String identifier) {
        return jwtUtil.validateToken(token, REFRESH_SECRET_KEY) && tokenRepository.existsById(identifier);
    }

    public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenType) {
        Cookie[] cookies = request.getCookies();
        return jwtUtil.resolveTokenFromCookie(cookies, tokenType);
    }

    public Authentication getAuthentication(String token) {
        UserDetails principal = customUserDetailsService.loadUserByUsername(getUserPk(token, ACCESS_SECRET_KEY));
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

    public String getIdentifierFromRefresh(String refreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(REFRESH_SECRET_KEY)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody()
                .getSubject();
    }

    public void logout(User requestUser, HttpServletResponse response) {
        tokenRepository.deleteById(requestUser.getIdentifier());

        Cookie accessCookie = jwtUtil.resetToken(ACCESS_PREFIX);
        Cookie refreshCookie = jwtUtil.resetToken(REFRESH_PREFIX);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}
