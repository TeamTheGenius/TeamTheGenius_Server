package com.genius.gitget.util.response.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class ListResponse<T> extends CommonResponse {
    private List<T> dataList;
    private int count;

    public ListResponse(List<T> dataList) {
        this.dataList = dataList;
        this.count = dataList.size();
    }
}
