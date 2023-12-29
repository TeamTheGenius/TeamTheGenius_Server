package com.genius.todoffin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TodoffinApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoffinApplication.class, args);
    }

}
