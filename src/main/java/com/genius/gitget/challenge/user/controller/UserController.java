package com.genius.gitget.challenge.user.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.CREATED;
import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.user.dto.SignupRequest;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.security.dto.TokenDTO;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @GetMapping("/auth/check-nickname")
    public ResponseEntity<CommonResponse> checkNicknameDuplicate(@RequestParam(value = "nickname") String nickname) {
        userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<SingleResponse<TokenDTO>> signup(@RequestBody SignupRequest signupRequest) {
        Long signupUserId = userService.signup(signupRequest);
        String identifier = userService.findUserById(signupUserId).getIdentifier();

        return ResponseEntity.ok().body(
                new SingleResponse<>(CREATED.getStatus(), CREATED.getMessage(), new TokenDTO(identifier))
        );
    }
}
