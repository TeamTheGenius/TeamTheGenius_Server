package com.genius.todoffin.security.service;

import com.genius.todoffin.user.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtGenerator {

    public String generateAccessToken(final Key ACCESS_SECRET, final long ACCESS_EXPIRATION, User requestUser) {
        Long now = System.currentTimeMillis();

        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(requestUser))
                .setSubject(String.valueOf(requestUser.getId()))
                .setExpiration(new Date(now + ACCESS_EXPIRATION))
                .signWith(ACCESS_SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(final Key REFRESH_SECRET, final long REFRESH_EXPIRATION, User requestUser) {
        Long now = System.currentTimeMillis();

        return Jwts.builder()
                .setHeader(createHeader())
                .setSubject(requestUser.getIdentifier())
                .setExpiration(new Date(now + REFRESH_EXPIRATION))
                .signWith(REFRESH_SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");
        return header;
    }

    private Map<String, Object> createClaims(User requestUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Identifier", requestUser.getIdentifier());
        claims.put("Role", requestUser.getRole());
        return claims;
    }
}
