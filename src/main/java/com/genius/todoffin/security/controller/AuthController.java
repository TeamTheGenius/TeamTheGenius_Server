package com.genius.todoffin.security.controller;

import static com.genius.todoffin.util.exception.SuccessCode.SUCCESS;

import com.genius.todoffin.security.domain.UserPrincipal;
import com.genius.todoffin.security.dto.TokenRequest;
import com.genius.todoffin.security.service.JwtService;
import com.genius.todoffin.user.domain.User;
import com.genius.todoffin.user.service.UserService;
import com.genius.todoffin.util.response.dto.CommonResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/auth")
    public ResponseEntity<CommonResponse> generateToken(HttpServletResponse response,
                                                        @RequestBody TokenRequest tokenRequest) {
        User requestUser = userService.findUserByIdentifier(tokenRequest.identifier());
        jwtService.generateAccessToken(response, requestUser);
        jwtService.generateRefreshToken(response, requestUser);

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(HttpServletResponse response) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        jwtService.logout(userPrincipal.getUser(), response);

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }
}
