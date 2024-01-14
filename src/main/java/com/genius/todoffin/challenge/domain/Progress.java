package com.genius.todoffin.challenge.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Progress {
    PRE_ACTIVITY("ROLE_PRE_ACTIVITY","시작 전" ),
    ACTIVITY("ROLE_ACTIVITY", "진행 중"),
    DONE("ROLE_DONE","종료");

    private final String key;
    private final String title;
}
