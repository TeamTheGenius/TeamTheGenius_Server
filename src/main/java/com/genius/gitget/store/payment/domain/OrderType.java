package com.genius.gitget.store.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderType {
    POINT("points", "포인트 충전"),
    ITEM("items", "아이템 구매");

    private final String key;
    private final String value;
}
