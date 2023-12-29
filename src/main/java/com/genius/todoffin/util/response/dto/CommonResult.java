package com.genius.todoffin.util.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum CommonResult {
    SUCCESS(1, "성공"),
    FAIL(0, "실패");

    private final int code;
    private final String message;
}