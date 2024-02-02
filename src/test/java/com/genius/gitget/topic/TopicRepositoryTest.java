package com.genius.gitget.topic;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback
public class TopicRepositoryTest {
    @Autowired
    private TopicRepository topicRepository;
    public Topic topic, topicA;

    @BeforeEach
    public void setup() {
        topic = Topic.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .build();

        topicA = Topic.builder()
                .title("1일 2알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(300)
                .build();
    }

    @Test
    public void 토픽_저장() {
        Topic savedTopic = topicRepository.save(topic);
        assertEquals(topic.getId(), savedTopic.getId());
        assertEquals(topic.getTitle(), savedTopic.getTitle());
        assertEquals(topic.getDescription(), savedTopic.getDescription());
    }

    @Test
    public void 토픽_수정() {
        Topic savedTopic = topicRepository.save(topic);
        if (!topic.getInstanceList().isEmpty()) {
            savedTopic.updateExistInstance("(수정) 하루에 두 문제씩 문제를 해결합니다.");
        } else {
            savedTopic.updateNotExistInstance("1일 1커밋", "하루에 1커밋 하기", "CS", "유의사항",300);
        }
        assertEquals(topic.getId(), savedTopic.getId());
        assertEquals(topic.getTitle(), savedTopic.getTitle());
        assertEquals(topic.getDescription(), savedTopic.getDescription());

    }

    @Test
    public void 토픽_삭제() {
        Topic savedTopic = topicRepository.save(topic);
        topicRepository.delete(savedTopic);
        Optional<Topic> byId = topicRepository.findById(1L);
        Assertions.assertThat(byId).isNotPresent();
    }


    @Test
    public void 토픽_리스트_조회() {
        for (int i=1; i<=10; i++) {
            topicRepository.save(
                    Topic.builder().title("user"+i+"L").build()
            );
        }
        Page<Topic> allById = topicRepository.findAllById(PageRequest.of(0, 5));
        Assertions.assertThat(allById.getSize()).isEqualTo(5);

        for (Topic topic1 : allById) {
            System.out.println("topic1.getTitle() = " + topic1.getTitle());
        }
    }

    @Test
    public void 토픽_단건_조회() {
        Topic savedTopic = topicRepository.save(topic);
        Optional<Topic> byId = topicRepository.findById(savedTopic.getId());

        Assertions.assertThat(byId.get().getTitle()).isEqualTo("1일 1알고리즘");
    }
}
