package com.genius.gitget.querydsl;

import com.genius.gitget.admin.topic.domain.QTopic;
import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.domain.QInstance;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstancePagingResponse;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.dto.search.QuerydslDTO;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import com.genius.gitget.challenge.instance.service.InstanceSearchService;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.global.file.domain.QFiles;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.util.file.FileTestUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Commit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.genius.gitget.admin.topic.domain.QTopic.*;
import static com.genius.gitget.challenge.instance.domain.Progress.*;
import static com.genius.gitget.challenge.instance.domain.QInstance.*;
import static com.genius.gitget.global.file.domain.QFiles.*;

@Transactional
@SpringBootTest
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    SearchRepository searchRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    InstanceSearchService instanceSearchService;
    @Autowired
    InstanceService instanceService;

    @Autowired
    FileTestUtil fileTestUtil;

    JPAQueryFactory queryFactory;

    QTopic t;
    QInstance i;
    QFiles f;

    @BeforeEach
    public void setup() throws IOException{

        queryFactory = new JPAQueryFactory(em);
        t = topic;
        i = instance;
        f = files;

        Topic topic = Topic.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .build();

        Instance instance = Instance.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .notice("유의사항")
                .progress(PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();


        Topic savedTopic = topicRepository.save(topic);

        createInstance(savedTopic, instance, instance.getTitle());
        createInstance(savedTopic, instance, instance.getTitle());

        Page<InstancePagingResponse> allInstances = instanceService.getAllInstances(PageRequest.of(0, 5));
        for (InstancePagingResponse allInstance : allInstances) {
            System.out.println("allInstance = " + allInstance);
        }
    }

    @Test
    public void findDtoByQuerydsl() throws IOException {
        List<QuerydslDTO> fetch = queryFactory.select(Projections.fields(QuerydslDTO.class,
                i.topic.id.as("topicId"), i.id.as("instanceId"), i.title, i.pointPerPerson, i.participantCount,
                f.fileURI, f.originalFilename, f.savedFilename))
                .from(i)
                .leftJoin(f)
                .on(i.files.id.eq(f.id))
                .where(i.progress.eq(Progress.valueOf("PREACTIVITY")), i.title.like("%아%"))
                .orderBy(i.startedDate.desc())
                .fetch();

        System.out.println("fetch = " + fetch.size());
        for (QuerydslDTO querydslDTO : fetch) {
            System.out.println("querydslDTO.toString() = " + querydslDTO.toString());
        }
    }


    @Test
    public void dynamicQuery_BooleanBuilder() {
        String instanceParam = "1일 1알고리즘";
        Integer pointParam = 100;

        List<Instance> result = searchInstance(instanceParam, pointParam);
        Assertions.assertThat(result.size()).isEqualTo(2);

    }

    private List<Instance> searchInstance(String instanceCond, Integer pointCond) {

        BooleanBuilder builder = new BooleanBuilder();
        if (instanceCond != null) {
            builder.and(i.title.eq(instanceCond));
        }
        if (pointCond != null) {
            builder.and(i.pointPerPerson.eq(pointCond));
        }

        return queryFactory
                .selectFrom(i)
                .where(builder)
                .fetch();
    }

    @Test
    public void dynamicQuery_WhereParam() {
        String instanceParam = "1일 1알고리즘";
        Integer pointParam = 100;

        List<Instance> result = searchInstance2(instanceParam, pointParam);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    private List<Instance> searchInstance2(String instanceCond, Integer pointCond) {
        return queryFactory
            .selectFrom(i)
            .where(instanceCondEq(instanceCond), pointCondEq(pointCond))
            .fetch();
    }

    private Predicate instanceCondEq(String instanceCond) {
        if (instanceCond == null) {
            return null;
        }
        return i.title.eq(instanceCond);
    }

    private Predicate pointCondEq(Integer pointCond) {
        if (pointCond == null) {
            return null;
        }
        return i.pointPerPerson.eq(pointCond);
    }

    @Test
    public void bulkUpdate() {
        long execute = queryFactory
                .update(i)
                .set(i.title, "1일 1커밋")
                .where(i.progress.notIn(ACTIVITY, DONE))
                .execute();

        em.flush();
        em.clear();

        List<Instance> list = queryFactory
                .selectFrom(i)
                .fetch();
        for (Instance instance : list) {
            System.out.println("instance = " + instance.getTitle());
        }
    }

    @Test
    public void bulkAdd() {
        queryFactory
                .update(i)
                .set(i.pointPerPerson, i.pointPerPerson.add(50))
                .execute();
    }

    @Test
    public void bulkDelete() {
        queryFactory
                .delete(i)
                .where(i.pointPerPerson.gt(100))
                .execute();
    }


    private void createInstance(Topic savedTopic, Instance instance, String title) throws IOException {
        instanceService.createInstance(
                InstanceCreateRequest.builder()
                        .topicId(savedTopic.getId())
                        .title(title)
                        .tags(instance.getTags())
                        .description(instance.getDescription())
                        .notice(instance.getNotice())
                        .pointPerPerson(instance.getPointPerPerson())
                        .startedAt(instance.getStartedDate())
                        .completedAt(instance.getCompletedDate()).build(),
                FileTestUtil.getMultipartFile("name"), "instance");
    }
}
