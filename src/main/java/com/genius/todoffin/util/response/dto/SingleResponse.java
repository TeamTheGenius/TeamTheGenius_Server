package com.genius.todoffin.util.response.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SingleResponse<T> extends CommonResponse {
    private T data;

    public SingleResponse(T data) {
        this.data = data;
    }
}
