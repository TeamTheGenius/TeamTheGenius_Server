package com.genius.gitget.challenge.instance.repository;

import static com.genius.gitget.challenge.instance.domain.Progress.ACTIVITY;
import static com.genius.gitget.challenge.instance.domain.Progress.DONE;
import static com.genius.gitget.challenge.instance.domain.Progress.PREACTIVITY;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.facade.InstanceFacade;
import com.genius.gitget.challenge.instance.service.InstanceSearchService;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import com.genius.gitget.util.file.FileTestUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.Builder;
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
    InstanceFacade instanceFacade;

    @Autowired
    FileTestUtil fileTestUtil;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void setup() throws IOException {

        queryFactory = new JPAQueryFactory(em);

        Topic topic = Topic.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .build();

        Instance instanceA = Instance.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .notice("유의사항")
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();

        Instance instanceB = Instance.builder()
                .title("1일 3알고리즘")
                .description("하루에 세 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(500)
                .notice("유의사항")
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(7))
                .build();

        Instance instanceC = Instance.builder()
                .title("2일 3알고리즘")
                .description("이것은 끝난 챌린지입니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(500)
                .notice("유의사항")
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(7))
                .build();

        Topic savedTopic = topicRepository.save(topic);

        instanceFacade.createInstance(createInstance(savedTopic, instanceA, instanceA.getTitle()),
                instanceA.getStartedDate().minusDays(3).toLocalDate());
        instanceFacade.createInstance(createInstance(savedTopic, instanceB, instanceB.getTitle()),
                instanceA.getStartedDate().minusDays(3).toLocalDate());
        instanceFacade.createInstance(createInstance(savedTopic, instanceB, instanceB.getTitle()),
                instanceA.getStartedDate().minusDays(3).toLocalDate());
        instanceFacade.createInstance(createInstance(savedTopic, instanceA, instanceA.getTitle()),
                instanceA.getStartedDate().minusDays(3).toLocalDate());
        instanceFacade.createInstance(createInstance(savedTopic, instanceB, "2일 3알고리즘"),
                instanceA.getStartedDate().minusDays(3).toLocalDate());
        instanceFacade.createInstance(createInstance(savedTopic, instanceC, instanceC.getTitle()),
                instanceA.getStartedDate().minusDays(3).toLocalDate());

    }

    @Builder
    private InstanceCreateRequest createInstance(Topic savedTopic, Instance instance, String title) {
        return InstanceCreateRequest.builder()
                .topicId(savedTopic.getId())
                .title(title)
                .tags(instance.getTags())
                .description(instance.getDescription())
                .notice(instance.getNotice())
                .pointPerPerson(instance.getPointPerPerson())
                .startedAt(instance.getStartedDate())
                .completedAt(instance.getCompletedDate()).build();
    }


    @Test
    public void 검색_조건_없이_테스트() throws Exception {
        for (int i = 0; i < 5; i++) {
            PageRequest pageRequest = PageRequest.of(i, 2);
            Page<Instance> result = searchRepository.search(null, null, pageRequest);
            for (Instance instance : result) {
                System.out.println("instanceSearchResponse = " + instance.getId());
            }
            System.out.println("========== " + i + 1 + " 번째 끝 =========");
        }
    }

    @Test
    public void 챌린지_제목으로_검색_테스트() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Instance> result = searchRepository.search(null, "2", pageRequest);
        int cnt = 0;
        for (Instance instance : result) {
            if (instance != null) {
                cnt++;
            }
        }
        Assertions.assertThat(cnt).isEqualTo(2);
    }


    @Test
    public void 챌린지_현황으로_검색_테스트() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Instance> result = searchRepository.search(PREACTIVITY, null, pageRequest);
        int cnt = 0;
        for (Instance instance : result) {
            if (instance != null) {
                cnt++;
            }
        }
        Assertions.assertThat(cnt).isEqualTo(6);
    }

    @Test
    public void 챌린지_현황으로_검색_테스트2() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Instance> result = searchRepository.search(DONE, null, pageRequest);
        int cnt = 0;
        for (Instance instance : result) {
            if (instance != null) {
                cnt++;
            }
        }
        Assertions.assertThat(cnt).isEqualTo(0);
    }

    @Test
    public void 챌린지_현황으로_검색_테스트3() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Instance> result = searchRepository.search(ACTIVITY, null, pageRequest);
        int cnt = 0;
        for (Instance instance : result) {
            if (instance != null) {
                cnt++;
            }
        }
        Assertions.assertThat(cnt).isEqualTo(0);
    }

    @Test
    public void 챌린지_현황과_챌린지_제목으로_검색_테스트() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Instance> result = searchRepository.search(PREACTIVITY, "3", pageRequest);
        int cnt = 0;
        for (Instance instance : result) {
            if (instance != null) {
                cnt++;
            }
        }
        Assertions.assertThat(cnt).isEqualTo(4);
    }

}
