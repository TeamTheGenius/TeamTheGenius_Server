package com.genius.todoffin.security.service;

import static com.genius.todoffin.security.constants.JwtRule.ACCESS_PREFIX;
import static com.genius.todoffin.security.constants.JwtRule.JWT_ISSUE_HEADER;
import static com.genius.todoffin.security.constants.JwtRule.REFRESH_PREFIX;
import static com.genius.todoffin.util.exception.ErrorCode.INVALID_CLAIM_JWT;
import static com.genius.todoffin.util.exception.ErrorCode.INVALID_EXPIRED_JWT;
import static com.genius.todoffin.util.exception.ErrorCode.INVALID_JWT;
import static com.genius.todoffin.util.exception.ErrorCode.INVALID_MALFORMED_JWT;
import static com.genius.todoffin.util.exception.ErrorCode.UNSUPPORTED_JWT;

import com.genius.todoffin.security.constants.JwtRule;
import com.genius.todoffin.security.domain.Token;
import com.genius.todoffin.security.repository.TokenRepository;
import com.genius.todoffin.user.domain.User;
import com.genius.todoffin.util.exception.BusinessException;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtGenerator jwtGenerator;
    private final TokenRepository tokenRepository;

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
        ResponseCookie cookie = setTokenToCookie(ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());
    }

    @Transactional
    public void generateRefreshToken(HttpServletResponse response, User requestUser) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET, REFRESH_EXPIRATION, requestUser);
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
        Key signingKey = jwtGenerator.getSigningKey(ACCESS_SECRET);
        return validateToken(token, signingKey);
    }

    public boolean validateRefreshToken(String token, String identifier) {
        Key signingKey = jwtGenerator.getSigningKey(REFRESH_SECRET);
        return validateToken(token, signingKey) && tokenRepository.existsById(identifier);
    }

    public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenType) {
        Cookie[] cookies = request.getCookies();
        if (tokenType == ACCESS_PREFIX) {
            return cookies[0].getValue();
        }
        return cookies[1].getValue();
    }

    public Authentication getAuthentication(String token) {
        Key key = jwtGenerator.getSigningKey(ACCESS_SECRET);
        UserDetails principal = customUserDetailsService.loadUserByUsername(getUserPk(token, key));
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

    public String getIdentifierFromToken(String refreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(REFRESH_SECRET)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody()
                .getSubject();
    }

    private boolean validateToken(String token, Key secretKey) {
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
}
