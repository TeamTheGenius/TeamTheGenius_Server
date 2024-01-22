package com.genius.gitget.util.response.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class PagingResponse<T> extends CommonResponse {
    private Page<T> data;

    public PagingResponse(Page<T> data) {
        this.data = data;
    }

    public PagingResponse(HttpStatus status, String message, Page<T> data) {
        super(status, message);
        this.data = data;
    }
}
