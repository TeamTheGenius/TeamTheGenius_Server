package com.genius.gitget.challenge.instance.repository;

import static com.genius.gitget.challenge.instance.domain.QInstance.instance;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class SearchRepositoryImpl implements SearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SearchRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Instance> search(Progress progressCond, String titleCond, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (progressCond != null) {
            builder.and(instance.progress.eq(progressCond));
        }
        if (titleCond != null) {
            builder.and(instance.title.contains(titleCond));
        }

        List<Instance> content = queryFactory
                .selectFrom(instance)
                .where(builder)
                .orderBy(instance.startedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(instance.count())
                .from(instance)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}