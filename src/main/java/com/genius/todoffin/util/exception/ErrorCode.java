package com.genius.todoffin.util.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다"),

    INVALID_EXPIRED_JWT(HttpStatus.BAD_REQUEST, "이미 만료된 JWT 입니다."),
    INVALID_MALFORMED_JWT(HttpStatus.BAD_REQUEST, "JWT의 구조가 유효하지 않습니다."),
    INVALID_CLAIM_JWT(HttpStatus.BAD_REQUEST, "JWT의 Claim이 유효하지 않습니다."),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "지원하지 않는 JWT 형식입니다."),
    INVALID_JWT(HttpStatus.BAD_REQUEST, "JWT가 유효하지 않습니다."),
    
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Cookie에 토큰이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
