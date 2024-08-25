package com.genius.gitget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@EnableCaching
@EnableMongoRepositories
@SpringBootApplication
public class GitgetApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitgetApplication.class, args);
	}
}
