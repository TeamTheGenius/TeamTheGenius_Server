package com.genius.todoffin.security.repository;

import com.genius.todoffin.security.domain.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends MongoRepository<Token, Long> {
}
