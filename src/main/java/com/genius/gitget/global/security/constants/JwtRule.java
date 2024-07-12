package com.genius.gitget.global.security.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JwtRule {

    ACCESS_HEADER("Authorization"),
    ACCESS_HEADER_PREFIX("Bearer "),

    JWT_ISSUE_HEADER("Set-Cookie"),
    JWT_RESOLVE_HEADER("Cookie"),
    ACCESS_PREFIX("access"),
    REFRESH_PREFIX("refresh");

    private final String value;
}
