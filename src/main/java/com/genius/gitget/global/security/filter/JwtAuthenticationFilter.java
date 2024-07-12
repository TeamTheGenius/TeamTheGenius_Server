package com.genius.gitget.global.security.filter;

import static com.genius.gitget.global.security.config.SecurityConfig.PERMITTED_URI;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.security.service.JwtFacade;
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
    private final JwtFacade jwtFacade;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isPermittedURI(request.getRequestURI())) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtFacade.resolveAccessToken(request);
        if (jwtFacade.validateAccessToken(accessToken)) {
            setAuthenticationToContext(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtFacade.resolveRefreshToken(request);
        User user = findUserByRefreshToken(refreshToken);

        if (jwtFacade.validateRefreshToken(refreshToken, user.getIdentifier())) {
            String reissuedAccessToken = jwtFacade.generateAccessToken(response, user);
            jwtFacade.generateRefreshToken(response, user);

            setAuthenticationToContext(reissuedAccessToken);
            filterChain.doFilter(request, response);
            return;
        }

        jwtFacade.logout(response, user.getIdentifier());
    }

    private boolean isPermittedURI(String requestURI) {
        return Arrays.stream(PERMITTED_URI)
                .anyMatch(permitted -> {
                    String replace = permitted.replace("*", "");
                    return requestURI.contains(replace) || replace.contains(requestURI);
                });
    }

    private User findUserByRefreshToken(String refreshToken) {
        String identifier = jwtFacade.getIdentifierFromRefresh(refreshToken);
        return userService.findUserByIdentifier(identifier);
    }

    private void setAuthenticationToContext(String accessToken) {
        Authentication authentication = jwtFacade.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
