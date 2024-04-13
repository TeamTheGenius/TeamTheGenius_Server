package com.genius.gitget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableMongoRepositories
public class GitgetApplication {
    // 자동화 배포 테스트
    public static void main(String[] args) {
        SpringApplication.run(GitgetApplication.class, args);
    }
}
