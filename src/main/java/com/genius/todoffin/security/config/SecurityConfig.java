package com.genius.todoffin.security.config;


import com.genius.todoffin.security.filter.JwtAuthenticationFilter;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

@Configuration
@Order(1)
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private static final String ALLOWED_ORIGIN = "http://localhost:5173";
    private static final String PERMIT_URI[] = {"/v3/**", "/swagger-ui/**", "/api/auth/**"};
    private static final String PERMITTED_ROLES[] = {"USER", "ADMIN"};
    private final CustomOAuth2UserService customOAuthService;
    private final OAuth2SuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(corsCustomizer -> corsCustomizer.configurationSource(
                                new CorsConfigurationSource() {
                                    @Override
                                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                        CorsConfiguration config = new CorsConfiguration();
                                        config.setAllowedOrigins(Collections.singletonList(ALLOWED_ORIGIN));
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
                        .requestMatchers(PERMIT_URI).permitAll()
                        .anyRequest().hasAnyRole(PERMITTED_ROLES))

                // JWT 사용으로 인한 세션 미사용
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // JWT 검증 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                // OAuth 로그인 설정
                .oauth2Login()
                .successHandler(successHandler)
                .authorizationEndpoint()

                .and()
                .userInfoEndpoint().userService(customOAuthService);

        return http.build();
    }
}