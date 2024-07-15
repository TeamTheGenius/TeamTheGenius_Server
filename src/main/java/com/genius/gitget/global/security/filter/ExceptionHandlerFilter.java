package com.genius.gitget.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.security.service.JwtFacade;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final JwtFacade jwtFacade;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            //NOTE: 인가 정보가 있는 경우 Logout 처리를 진행...? JWT 관련 예외에서만 진행되는게 맞나? 확인해봐야지
            if (authentication != null) {
                UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
                String identifier = principal.getUser().getIdentifier();
                jwtFacade.logout(response, identifier);
            }

            log.error("ExceptionHandlerFilter에서 작동: " + e.getMessage(), e);
            setErrorResponse(response, e);
        }
    }

    private void setErrorResponse(HttpServletResponse response, BusinessException e) throws IOException {
        CommonResponse commonResponse = new CommonResponse(e.getStatus(), e.getStatus().value(), e.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(e.getStatus().value());
        response.setContentType("application/json; charset=UTF-8");

        response.getWriter().write(
                objectMapper.writeValueAsString(commonResponse)
        );
    }
}
