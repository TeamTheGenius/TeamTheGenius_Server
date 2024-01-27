package com.genius.gitget.global.util.config;

import com.genius.gitget.global.util.formatter.LocalDateFormatter;
import com.genius.gitget.global.util.formatter.LocalDateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public LocalDateFormatter localDateFormatter() {
        return new LocalDateFormatter();
    }

    @Bean
    public LocalDateTimeFormatter localDateTimeFormatter() {
        return new LocalDateTimeFormatter();
    }
}
