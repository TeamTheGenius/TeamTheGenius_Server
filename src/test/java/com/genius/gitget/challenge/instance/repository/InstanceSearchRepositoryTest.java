package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;

import java.io.IOException;
import java.time.LocalDateTime;

import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.service.InstanceSearchService;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.util.file.FileTestUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static com.genius.gitget.admin.topic.domain.QTopic.topic;
import static com.genius.gitget.challenge.instance.domain.Progress.*;
import static com.genius.gitget.challenge.instance.domain.QInstance.instance;
import static com.genius.gitget.global.file.domain.QFiles.files;

@SpringBootTest
@Transactional
@Rollback
@Slf4j
public class InstanceSearchRepositoryTest {
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

    // @BeforeEach
    public void setup() throws IOException{

        queryFactory = new JPAQueryFactory(em);

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

        instanceService.getAllInstances(PageRequest.of(0, 5));
    }


    @Test
    public void 검색_조건_없이_테스트() throws Exception {
        for (int i = 0; i<5; i++) {
            PageRequest pageRequest = PageRequest.of(i, 2);
            Page<InstanceSearchResponse> result = searchRepository.Search(null, null, pageRequest);
            for (InstanceSearchResponse instanceSearchResponse : result) {
                System.out.println("instanceSearchResponse = " + instanceSearchResponse.getInstanceId());
            }
            System.out.println("========== " + i+1 + " 번째 끝 =========");
        }
    }

    @Test
    public void 챌린지_제목으로_검색_테스트() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<InstanceSearchResponse> result = searchRepository.Search(null, "리", pageRequest);
        int cnt = 0;
        for (InstanceSearchResponse instanceSearchResponse : result) {
            if (instanceSearchResponse != null) cnt++;
        }
        Assertions.assertThat(cnt).isEqualTo(2);
    }

    @Test
    public void 챌린지_현황으로_검색_테스트1() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<InstanceSearchResponse> result = searchRepository.Search(PREACTIVITY, null, pageRequest);
        int cnt = 0;
        for (InstanceSearchResponse instanceSearchResponse : result) {
            if (instanceSearchResponse != null) cnt++;
        }
        Assertions.assertThat(cnt).isEqualTo(4);
    }

    @Test
    public void 챌린지_현황으로_검색_테스트2() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<InstanceSearchResponse> result = searchRepository.Search(DONE, null, pageRequest);
        int cnt = 0;
        for (InstanceSearchResponse instanceSearchResponse : result) {
            if (instanceSearchResponse != null) cnt++;
        }
        Assertions.assertThat(cnt).isEqualTo(1);
    }

    @Test
    public void 챌린지_현황으로_검색_테스트3() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<InstanceSearchResponse> result = searchRepository.Search(ACTIVITY, null, pageRequest);
        int cnt = 0;
        for (InstanceSearchResponse instanceSearchResponse : result) {
            if (instanceSearchResponse != null) cnt++;
        }
        Assertions.assertThat(cnt).isEqualTo(0);
    }

    @Test
    public void 챌린지_현황과_챌린지_제목으로_검색_테스트() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<InstanceSearchResponse> result = searchRepository.Search(PREACTIVITY, "1", pageRequest);
        int cnt = 0;
        for (InstanceSearchResponse instanceSearchResponse : result) {
            if (instanceSearchResponse != null) cnt++;
        }
        Assertions.assertThat(cnt).isEqualTo(3);
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
