package com.genius.todoffin.user.controller;

import static com.genius.todoffin.util.exception.SuccessCode.CREATED;
import static com.genius.todoffin.util.exception.SuccessCode.SUCCESS;

import com.genius.todoffin.user.dto.SignupRequest;
import com.genius.todoffin.user.service.UserService;
import com.genius.todoffin.util.response.dto.CommonResponse;
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
    public ResponseEntity<CommonResponse> signup(@RequestBody SignupRequest signupRequest) {
        userService.signup(signupRequest);
        return ResponseEntity.ok().body(
                new CommonResponse(CREATED.getStatus(), CREATED.getMessage())
        );
    }
}
