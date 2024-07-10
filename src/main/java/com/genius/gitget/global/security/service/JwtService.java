package com.genius.gitget.global.security.service;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.security.constants.JwtRule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface JwtService {
    String generateAccessToken(HttpServletResponse response, User user);

    String generateRefreshToken(HttpServletResponse response, User user);

    String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenPrefix);

    String getIdentifierFromRefresh(String refreshToken);

    boolean validateAccessToken(String accessToken);

    boolean validateRefreshToken(String refreshToken, String identifier);

    void logout(HttpServletResponse response, String identifier);

    Authentication getAuthentication(String accessToken);
}
