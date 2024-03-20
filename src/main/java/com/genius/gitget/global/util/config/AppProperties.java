package com.genius.gitget.global.util.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public final class AppProperties {
    private final String ERROR_PARAM_PREFIX = "error";

    private final String BASE_URL;
    private final String SIGNUP_URL;
    private final String AUTH_URL;

    public AppProperties(@Value("${url.base}") String BASE_URL,
                         @Value("${url.path.signup}") String SIGNUP_URL,
                         @Value("${url.path.auth}") String AUTH_URL) {
        this.BASE_URL = BASE_URL;
        this.SIGNUP_URL = BASE_URL + SIGNUP_URL;
        this.AUTH_URL = BASE_URL + AUTH_URL;
    }
}
