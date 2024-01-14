package com.genius.todoffin.security.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Token")
@Getter
public class Token {
    @Id
    private Long id;

    private String token;
}
