package com.genius.todoffin.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenUtil {
    private final String ACCESS_SECRET;
    private final long ACCESS_EXPIRATION;
    private final String REFRESH_SECRET;
    private final long REFRESH_EXPIRATION;


    public JwtTokenUtil(
            @Value("${jwt.access-secret}") String ACCESS_SECRET,
            @Value("${jwt.refresh-secret}") String REFRESH_SECRET,
            @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
            @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION
    ) {
        this.ACCESS_SECRET = ACCESS_SECRET;
        this.REFRESH_SECRET = REFRESH_SECRET;
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
    }


    public String generateRefreshToken() {
        Long now = System.currentTimeMillis();

        return Jwts.builder()
                .setHeader(createHeader())
                .setIssuedAt(new Date())
                .setExpiration(new Date(now + REFRESH_EXPIRATION))
                .signWith(getSecretKey(REFRESH_SECRET), SignatureAlgorithm.HS512)
                .compact();
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");
        return header;
    }

    private Key getSecretKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
