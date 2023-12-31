package com.genius.todoffin.util.response.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@RequiredArgsConstructor
public class PagingResponse<T> extends CommonResponse {
    private Page<T> data;

    public PagingResponse(Page<T> data) {
        this.data = data;
    }
}
