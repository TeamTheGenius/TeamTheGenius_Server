package com.genius.gitget.global.security.service;

import static com.genius.gitget.global.util.exception.ErrorCode.INVALID_EXPIRED_JWT;
import static com.genius.gitget.global.util.exception.ErrorCode.INVALID_JWT;
import static com.genius.gitget.global.util.exception.ErrorCode.JWT_TOKEN_NOT_FOUND;

import com.genius.gitget.global.security.constants.JwtRule;
import com.genius.gitget.global.security.constants.TokenStatus;
import com.genius.gitget.global.util.exception.BusinessException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    public TokenStatus getTokenStatus(String token, Key secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return TokenStatus.AUTHENTICATED;
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            log.error(INVALID_EXPIRED_JWT.getMessage());
            return TokenStatus.EXPIRED;
        } catch (JwtException e) {
            throw new BusinessException(INVALID_JWT);
        }
    }

    public String resolveTokenFromCookie(Cookie[] cookies, JwtRule tokenPrefix) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(tokenPrefix.getValue()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new BusinessException(JWT_TOKEN_NOT_FOUND));
    }

    public Key getSigningKey(String secretKey) {
        String encodedKey = encodeToBase64(secretKey);
        return Keys.hmacShaKeyFor(encodedKey.getBytes(StandardCharsets.UTF_8));
    }

    private String encodeToBase64(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Cookie resetCookie(JwtRule tokenPrefix) {
        Cookie cookie = new Cookie(tokenPrefix.getValue(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
