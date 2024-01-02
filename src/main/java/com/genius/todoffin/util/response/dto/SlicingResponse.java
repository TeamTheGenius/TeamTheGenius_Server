package com.genius.todoffin.util.response.dto;

import org.springframework.data.domain.Slice;

public class SlicingResponse<T> extends CommonResponse {
    private Slice<T> data;

    public SlicingResponse(Slice<T> data) {
        this.data = data;
    }
}
