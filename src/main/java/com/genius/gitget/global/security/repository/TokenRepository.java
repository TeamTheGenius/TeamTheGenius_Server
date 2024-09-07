package com.genius.gitget.global.security.repository;

import com.genius.gitget.global.security.domain.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByIdentifier(String identifier);
}
