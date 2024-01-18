package com.genius.todoffin.topic;

import com.genius.todoffin.topic.domain.Topic;
import com.genius.todoffin.topic.repository.TopicRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class TopicRepositoryTest {

    @Autowired
    TopicRepository topicRepository;

    @Test
    public void 토픽_저장() {
        Topic topic = new Topic("1일 1커밋", "챌린지입니다.", "BE, CS", 500);
        Topic savedTopic = topicRepository.save(topic);

        Assertions.assertThat(topic.getTitle()).isEqualTo(savedTopic.getTitle());
    }
}
