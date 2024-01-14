package com.genius.todoffin.security.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "Token")
@Getter
public class Token {
    @MongoId
    private String identifier;

    private String token;

    @Builder
    public Token(String identifier, String token) {
        this.identifier = identifier;
        this.token = token;
    }
}
