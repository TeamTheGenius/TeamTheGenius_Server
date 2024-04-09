package com.genius.gitget.global.util.config;

import com.genius.gitget.global.util.formatter.LocalDateFormatter;
import com.genius.gitget.global.util.formatter.LocalDateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

@Configuration
@PropertySource("classpath:application-common.yml")
@RequiredArgsConstructor
public class AppConfig {
    private final Environment env;

    @Bean
    public AesBytesEncryptor aesBytesEncryptor() {
        return new AesBytesEncryptor(
                env.getProperty("github.encryptSecretKey"),
                env.getProperty("github.salt"));
    }

    @Bean
    public LocalDateFormatter localDateFormatter() {
        return new LocalDateFormatter();
    }

    @Bean
    public LocalDateTimeFormatter localDateTimeFormatter() {
        return new LocalDateTimeFormatter();
    }
}
