package com.genius.todoffin.security.filter;

import static com.genius.todoffin.security.config.SecurityConfig.PERMITTED_URI;

import com.genius.todoffin.security.constants.JwtRule;
import com.genius.todoffin.security.service.JwtService;
import com.genius.todoffin.user.domain.User;
import com.genius.todoffin.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isPermittedURI(request.getRequestURI())) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtService.resolveTokenFromCookie(request, JwtRule.ACCESS_PREFIX);
        if (jwtService.validateAccessToken(accessToken)) {
            Authentication authentication = jwtService.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);
        String identifier = jwtService.getIdentifierFromToken(refreshToken);
        if (jwtService.validateRefreshToken(refreshToken, identifier)) {
            User user = userService.findUserByIdentifier(identifier);
            jwtService.generateAccessToken(response, user);
            jwtService.generateRefreshToken(response, user);
        }

        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private boolean isPermittedURI(String requestURI) {
        return Arrays.stream(PERMITTED_URI)
                .anyMatch(permitted -> permitted.contains(requestURI));
    }
}
