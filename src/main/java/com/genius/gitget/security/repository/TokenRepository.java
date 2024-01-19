package com.genius.gitget.security.repository;

import com.genius.gitget.security.domain.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByIdentifier(String identifier);
}
