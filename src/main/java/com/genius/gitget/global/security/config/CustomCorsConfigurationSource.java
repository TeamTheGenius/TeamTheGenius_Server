package com.genius.gitget.global.security.config;

import static com.genius.gitget.global.security.constants.JwtRule.ACCESS_HEADER;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Component
public class CustomCorsConfigurationSource implements CorsConfigurationSource {
    private final String ALLOWED_ORIGIN;
    private final List<String> ALLOWED_METHODS = List.of("POST", "GET", "PATCH", "OPTIONS", "DELETE");

    public CustomCorsConfigurationSource(@Value("${url.base}") String BASE_URL) {
        ALLOWED_ORIGIN = BASE_URL;
    }

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList(ALLOWED_ORIGIN));
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.addAllowedHeader(ACCESS_HEADER.getValue());
        config.setMaxAge(3600L);
        return config;
    }
}
