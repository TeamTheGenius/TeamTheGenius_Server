package com.genius.todoffin.security.repository;

import com.genius.todoffin.security.domain.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByIdentifier(String identifier);
}
