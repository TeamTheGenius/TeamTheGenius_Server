package com.genius.gitget.global.security.controller;

import static com.genius.gitget.global.util.exception.ErrorCode.NOT_AUTHENTICATED_USER;
import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.security.dto.AuthResponse;
import com.genius.gitget.global.security.dto.TokenRequest;
import com.genius.gitget.global.security.service.JwtFacade;
import com.genius.gitget.global.util.annotation.GitGetUser;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final JwtFacade jwtFacade;

    @PostMapping("/auth")
    public ResponseEntity<SingleResponse<AuthResponse>> generateToken(HttpServletResponse response,
                                                                      @RequestBody TokenRequest tokenRequest) {
        User user = userService.findUserByIdentifier(tokenRequest.identifier());
        if (!user.isRegistered()) {
            throw new BusinessException(NOT_AUTHENTICATED_USER);
        }

        jwtFacade.generateAccessToken(response, user);
        jwtFacade.generateRefreshToken(response, user);
        jwtFacade.setReissuedHeader(response);

        AuthResponse authResponse = userService.getUserAuthInfo(user.getIdentifier());

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), authResponse)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@GitGetUser User user, HttpServletResponse response) {
        jwtFacade.logout(response, user.getIdentifier());

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }

    @GetMapping("/auth/health-check")
    public String healthCheck() {
        return "health-check-ok";
    }
}
