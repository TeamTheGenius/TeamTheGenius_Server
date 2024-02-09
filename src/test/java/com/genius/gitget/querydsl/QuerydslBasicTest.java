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
import com.genius.gitget.challenge.instance.dto.search.QQuerydslDTO;
import com.genius.gitget.challenge.instance.dto.search.QuerydslDTO;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import com.genius.gitget.challenge.instance.service.InstanceSearchService;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.global.file.domain.QFiles;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.util.file.FileTestUtil;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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

        t = QTopic.topic;
        i = QInstance.instance;
        f = QFiles.files;


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
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();


        Topic savedTopic = topicRepository.save(topic);

        createInstance(savedTopic, instance, instance.getTitle());
        createInstance(savedTopic, instance, "고리");

        Page<InstancePagingResponse> allInstances = instanceService.getAllInstances(PageRequest.of(0, 5));
        for (InstancePagingResponse allInstance : allInstances) {
            System.out.println("allInstance = " + allInstance);
        }
        // Page<InstanceSearchResponse> orderList = instanceSearchService.searchInstances("고리", "preactivity", , "instance",
        // PageRequest.of(0, 3));
    }

    @Test
    public void findDtoByQuerydsl() throws IOException {

        List<QuerydslDTO> fetch = queryFactory.select(new QQuerydslDTO(
                        i.topic.id,
                        i.id,
                        i.files.id,
                        i.title,
                        i.pointPerPerson,
                        i.participantCount))
                .from(i)
                .fetch();

        for (QuerydslDTO querydslDTO : fetch) {
            System.out.println("querydslDTO.toString() = " + querydslDTO.toString());
        }
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
