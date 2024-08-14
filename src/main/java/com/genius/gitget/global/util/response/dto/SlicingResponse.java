package com.genius.gitget.global.util.response.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class SlicingResponse<T> extends CommonResponse {
    private Slice<T> data;

    public SlicingResponse(HttpStatus status, String message, Slice<T> data) {
        super(status, message);
        this.data = data;
    }

    public SlicingResponse(Slice<T> data) {
        this.data = data;
    }
}
