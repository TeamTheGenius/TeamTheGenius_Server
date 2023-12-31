package com.genius.todoffin.security.config;


import com.genius.todoffin.security.handler.OAuth2SuccessHandler;
import com.genius.todoffin.security.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

@Configuration
@Order(1)
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private static final String permitURI[] = {"/v3/**", "/swagger-ui/**"};
    private static final String permittedRoles[] = {"USER", "ADMIN"};
    private final CustomOAuth2UserService customOAuthService;
    private final OAuth2SuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(corsCustomizer -> corsCustomizer.configurationSource(
                                new CorsConfigurationSource() {
                                    @Override
                                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                        CorsConfiguration config = new CorsConfiguration();
                                        config.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                                        config.setAllowedMethods(Collections.singletonList("*"));
                                        config.setAllowCredentials(true);
                                        config.setAllowedHeaders(Collections.singletonList("*"));
                                        config.setMaxAge(3600L);
                                        return config;
                                    }
                                }
                        )
                )
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .anonymous().and()
                .authorizeHttpRequests(request -> request
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(permitURI).permitAll()
                        .anyRequest().hasAnyRole(permittedRoles))

                // JWT 사용으로 인한 세션 미사용
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // OAuth 로그인 설정
                .oauth2Login()
                .successHandler(successHandler)
                .authorizationEndpoint()

                .and()
                .userInfoEndpoint().userService(customOAuthService);

        return http.build();
    }
}