package com.genius.gitget.challenge.item.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipStatus {
    UNAVAILABLE("장착 불가"),
    AVAILABLE("장착"),
    IN_USE("장착 중");

    private final String tag;
}
