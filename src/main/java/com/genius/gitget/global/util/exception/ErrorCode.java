package com.genius.gitget.global.util.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 토픽을 찾을 수 없습니다."),
    TOPIC_HAVE_INSTANCE(HttpStatus.BAD_REQUEST, "해당 토픽은 인스턴스를 가지고 있으므로 삭제할 수 없습니다."),

    INSTANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 인스턴스를 찾을 수 없습니다."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다"),
    NOT_AUTHENTICATED_USER(HttpStatus.BAD_REQUEST, "인증 가능한 사용자가 아닙니다."),

    INVALID_EXPIRED_JWT(HttpStatus.BAD_REQUEST, "이미 만료된 JWT 입니다."),
    INVALID_MALFORMED_JWT(HttpStatus.BAD_REQUEST, "JWT의 구조가 유효하지 않습니다."),
    INVALID_CLAIM_JWT(HttpStatus.BAD_REQUEST, "JWT의 Claim이 유효하지 않습니다."),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "지원하지 않는 JWT 형식입니다."),
    INVALID_JWT(HttpStatus.BAD_REQUEST, "JWT가 유효하지 않습니다."),

    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Cookie에 토큰이 존재하지 않습니다."),

    FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 파일(이미지)이 존재하지 않습니다."),
    NOT_SUPPORTED_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 확장자입니다."),
    NOT_SUPPORTED_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 타입입니다."),
    FILE_NOT_DELETED(HttpStatus.BAD_REQUEST, "파일(이미지)이 정상적으로 삭제되지 않았습니다."),

    GITHUB_CONNECTION_FAILED(HttpStatus.BAD_REQUEST, "Github 연결이 실패했습니다."),
    GITHUB_ID_INCORRECT(HttpStatus.BAD_REQUEST, "소셜로그인에 사용한 Github 계정과 일치하지 않습니다."),
    GITHUB_REPOSITORY_INCORRECT(HttpStatus.BAD_REQUEST, "해당 레포지토리와 연결이 되지 않습니다.");


    private final HttpStatus status;
    private final String message;
}
