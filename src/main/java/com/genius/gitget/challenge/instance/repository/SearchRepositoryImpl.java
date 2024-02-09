package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SearchRepositoryImpl implements SearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SearchRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Instance> basicSearch(String title, Pageable pageable) {
        // return queryFactory
        return null;
    }
}
