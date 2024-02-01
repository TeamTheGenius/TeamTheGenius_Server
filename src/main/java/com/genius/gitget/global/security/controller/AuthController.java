package com.genius.gitget.global.security.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.security.dto.TokenDTO;
import com.genius.gitget.global.security.service.JwtService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
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
                                                        @RequestBody TokenDTO tokenRequest) {
        User requestUser = userService.findUserByIdentifier(tokenRequest.identifier());
        jwtService.validateUser(requestUser);

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

    @GetMapping("/test")
    public ResponseEntity<CommonResponse> test() {

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), "TEST 성공")
        );
    }
}