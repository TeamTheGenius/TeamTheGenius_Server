package com.genius.todoffin.security.service;

import static com.genius.todoffin.util.exception.ErrorCode.INVALID_CLAIM_JWT;
import static com.genius.todoffin.util.exception.ErrorCode.INVALID_EXPIRED_JWT;
import static com.genius.todoffin.util.exception.ErrorCode.INVALID_JWT;
import static com.genius.todoffin.util.exception.ErrorCode.INVALID_MALFORMED_JWT;
import static com.genius.todoffin.util.exception.ErrorCode.TOKEN_NOT_FOUND;
import static com.genius.todoffin.util.exception.ErrorCode.UNSUPPORTED_JWT;

import com.genius.todoffin.security.constants.JwtRule;
import com.genius.todoffin.util.exception.BusinessException;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtUtil {

    public boolean validateToken(String token, Key secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error(INVALID_EXPIRED_JWT.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            throw new BusinessException(INVALID_MALFORMED_JWT);
        } catch (ClaimJwtException e) {
            throw new BusinessException(INVALID_CLAIM_JWT);
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(UNSUPPORTED_JWT);
        } catch (JwtException e) {
            throw new BusinessException(INVALID_JWT);
        }
    }

    public String resolveTokenFromCookie(Cookie[] cookies, JwtRule tokenType) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(tokenType.getValue()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new BusinessException(TOKEN_NOT_FOUND));
    }

    public Key getSigningKey(String secretKey) {
        String encodedKey = encodeToBase64(secretKey);
        return Keys.hmacShaKeyFor(encodedKey.getBytes(StandardCharsets.UTF_8));
    }

    private String encodeToBase64(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Cookie resetToken(JwtRule tokenType) {
        Cookie cookie = new Cookie(tokenType.getValue(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
