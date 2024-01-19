package com.genius.gitget.util.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // 200 OK
    SUCCESS(HttpStatus.OK, "OK", "요청이 정상적으로 처리되었습니다.")

    // 201 CREATED
    , CREATED(HttpStatus.CREATED, "CREATED", "정상적으로 생성되었습니다.");


    private final HttpStatus status;
    private final String key;
    private final String message;
}

