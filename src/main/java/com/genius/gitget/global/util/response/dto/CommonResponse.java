package com.genius.gitget.global.util.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private HttpStatus code;
    private int resultCode;
    private String message;

    public CommonResponse(HttpStatus code, String message) {
        this.code = code;
        this.resultCode = code.value();
        this.message = message;
    }
}