package com.genius.gitget.global.util.exception;

import com.genius.gitget.global.util.response.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class BusinessExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<CommonResponse> globalBusinessExceptionHandler(BusinessException e) {
        log.error("[ERROR]" + e.getMessage(), e);

        return ResponseEntity.badRequest().body(
                new CommonResponse(e.getStatus(), e.getMessage())
        );
    }
}
