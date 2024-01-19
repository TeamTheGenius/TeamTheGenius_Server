package com.genius.gitget.security.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JwtRule {
    JWT_ISSUE_HEADER("Set-Cookie"),
    JWT_RESOLVE_HEADER("Cookie"),
    ACCESS_PREFIX("access"),
    REFRESH_PREFIX("refresh");

    private final String value;
}
