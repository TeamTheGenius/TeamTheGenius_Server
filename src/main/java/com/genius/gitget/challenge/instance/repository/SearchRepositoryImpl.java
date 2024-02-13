package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.dto.search.QInstanceSearchResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import static com.genius.gitget.challenge.instance.domain.QInstance.instance;
import static com.genius.gitget.global.file.domain.QFiles.files;

import java.util.List;

public class SearchRepositoryImpl implements SearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SearchRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<InstanceSearchResponse> Search(Progress progressCond, String titleCond, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (progressCond != null) {
            builder.and(instance.progress.eq(progressCond));
        }
        if (titleCond != null) {
            builder.and(instance.title.contains(titleCond));
        }

        List<InstanceSearchResponse> content = queryFactory
                .select(new QInstanceSearchResponse(
                        instance.topic.id, instance.id, instance.title, instance.pointPerPerson, instance.participantCount,
                        instance.files))
                .from(instance)
                .leftJoin(instance.files, files)
                .on(instance.files.id.eq(files.id))
                .where(builder)
                .orderBy(instance.startedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(instance.count())
                .from(instance)
                .leftJoin(instance.files, files)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
