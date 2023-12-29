package com.genius.todoffin.util.exception;

import org.springframework.http.HttpStatus;


public class BusinessException extends RuntimeException {
    private HttpStatus status;

    public BusinessException() {
        super();
    }

    public BusinessException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = status;
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
}