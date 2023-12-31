package com.genius.todoffin.util.exception;

import com.genius.todoffin.util.response.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class BusinessExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    protected CommonResponse globalBusinessExceptionHandler(BusinessException e) {
        log.info("[ERROR]" + e.getMessage(), e);

        return new CommonResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
