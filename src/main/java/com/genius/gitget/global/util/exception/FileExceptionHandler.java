package com.genius.gitget.global.util.exception;

import static com.genius.gitget.global.util.exception.ErrorCode.FILE_MAX_SIZE_EXCEED;

import com.genius.gitget.global.util.response.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class FileExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<CommonResponse> globalExceptionHandler(Exception e) {
        log.error("Multipart 용량이 최대 크기를 초과하여 예외가 발생했습니다.");
        return ResponseEntity.badRequest().body(
                new CommonResponse(HttpStatus.BAD_REQUEST, FILE_MAX_SIZE_EXCEED.getMessage()));
    }
}
