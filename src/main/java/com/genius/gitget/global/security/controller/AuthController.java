package com.genius.gitget.global.security.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.facade.UserFacade;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.security.dto.AuthResponse;
import com.genius.gitget.global.security.dto.TokenRequest;
import com.genius.gitget.global.security.service.JwtFacade;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserFacade userFacade;
    private final JwtFacade jwtFacade;

    @PostMapping("/auth")
    public ResponseEntity<SingleResponse<AuthResponse>> generateToken(HttpServletResponse response,
                                                                      @RequestBody TokenRequest tokenRequest) {

        User authUser = userFacade.getAuthUser(tokenRequest.identifier());

        jwtFacade.generateAccessToken(response, authUser);
        jwtFacade.generateRefreshToken(response, authUser);
        jwtFacade.setReissuedHeader(response);

        AuthResponse authResponse = userFacade.getUserAuthInfo(authUser.getIdentifier());

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), authResponse)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletResponse response) {
        jwtFacade.logout(response, userPrincipal.getUser().getIdentifier());

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }

    @GetMapping("/auth/health-check")
    public String healthCheck() {
        return "health-check-ok";
    }
}
