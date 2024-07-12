package com.genius.gitget.global.security.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JwtRule {

    ACCESS_HEADER("Authorization"),
    ACCESS_PREFIX("Bearer "),

    REFRESH_PREFIX("refresh"),

    REFRESH_ISSUE("Set-Cookie"),
    REFRESH_RESOLVE("Cookie");

    private final String value;
}
