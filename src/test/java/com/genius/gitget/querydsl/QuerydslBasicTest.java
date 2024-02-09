package com.genius.gitget.querydsl;

import com.genius.gitget.challenge.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.html.parser.Entity;

import static com.genius.gitget.challenge.user.domain.Role.ADMIN;
import static com.genius.gitget.challenge.user.domain.Role.USER;
import static com.genius.gitget.global.security.constants.ProviderInfo.GOOGLE;
import static com.genius.gitget.global.security.constants.ProviderInfo.NAVER;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;
    User userA, userB;

    @BeforeEach
    public void setup() {
        userA = userA();
        userB = userB();

        em.persist(userA);
        em.persist(userB);
    }

    @Test
    public void startQuerydsl() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    }


    private User userA() {
        return User.builder().identifier("neo5188@gmail.com")
                .providerInfo(NAVER)
                .nickname("kimdozzi")
                .information("백엔드")
                .tags("운동")
                .role(ADMIN)
                .build();
    }

    private User userB() {
        return User.builder().identifier("ssang23@naver.com")
                .providerInfo(GOOGLE)
                .nickname("SEONG")
                .information("프론트엔드")
                .tags("영화")
                .role(USER)
                .build();
    }
}
