package com.genius.gitget.security.config;


import com.genius.gitget.security.filter.JwtAuthenticationFilter;
import com.genius.gitget.security.handler.OAuth2FailureHandler;
import com.genius.gitget.security.handler.OAuth2SuccessHandler;
import com.genius.gitget.security.service.CustomOAuth2UserService;
import com.genius.gitget.security.service.JwtService;
import com.genius.gitget.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@Order(1)
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    public static final String PERMITTED_URI[] = {"/v3/**", "/swagger-ui/**", "/api/auth/**"};
    private static final String PERMITTED_ROLES[] = {"USER", "ADMIN"};
    private final CustomCorsConfigurationSource customCorsConfigurationSource;
    private final CustomOAuth2UserService customOAuthService;
    private final JwtService jwtService;
    private final UserService userService;
    private final OAuth2SuccessHandler successHandler;
    private final OAuth2FailureHandler failureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(corsCustomizer -> corsCustomizer
                        .configurationSource(customCorsConfigurationSource)
                )
                .csrf(CsrfConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(PERMITTED_URI).permitAll()
                        .anyRequest().hasAnyRole(PERMITTED_ROLES))

                // JWT 사용으로 인한 세션 미사용
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // JWT 검증 필터 추가
//                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userService),
//                        UsernamePasswordAuthenticationFilter.class)

                // OAuth 로그인 설정
                .oauth2Login(customConfigurer -> customConfigurer
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .userInfoEndpoint(endpointConfig -> endpointConfig.userService(customOAuthService))
                );

        return http.build();
    }
}