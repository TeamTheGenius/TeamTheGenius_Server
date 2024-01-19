package com.genius.gitget.util.config;

import com.genius.gitget.util.formatter.LocalDateFormatter;
import com.genius.gitget.util.formatter.LocalDateTimeFormatter;
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
