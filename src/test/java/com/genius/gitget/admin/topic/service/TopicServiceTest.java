package com.genius.gitget.admin.topic.service;

import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.dto.TopicCreateRequest;
import com.genius.gitget.topic.dto.TopicDetailResponse;
import com.genius.gitget.topic.dto.TopicUpdateRequest;
import com.genius.gitget.topic.repository.TopicRepository;
import com.genius.gitget.topic.service.TopicService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
@Rollback
public class TopicServiceTest {
    Topic topic, topicA;
    String fileType;
    @Autowired
    TopicService topicService;
    @Autowired
    TopicRepository topicRepository;

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

        fileType = "topic";
    }

    @Test
    public void 토픽_생성() throws Exception {
        //given
        TopicCreateRequest topicCreateRequest = getTopicCreateRequest();

        Long savedTopicId = topicService.createTopic(topicCreateRequest);

        //when
        TopicDetailResponse topicById = topicService.getOneTopic(savedTopicId);

        //then
        Assertions.assertThat(topicById.title()).isEqualTo(topicCreateRequest.title());
    }

    @Test
    public void 토픽_수정() throws Exception {
        //given
        TopicCreateRequest topicCreateRequest = getTopicCreateRequest();
        Long savedTopicId = topicService.createTopic(topicCreateRequest);

        //when
        TopicUpdateRequest topicUpdateRequest = TopicUpdateRequest.builder()
                .title("1일 5커밋")
                .description(topic.getDescription())
                .tags(topic.getTags())
                .pointPerPerson(topic.getPointPerPerson())
                .notice(topic.getNotice()).build();

        topicService.updateTopic(savedTopicId, topicUpdateRequest);

        //then
        Optional<Topic> findTopic = topicRepository.findById(savedTopicId);
        Topic findUpdatedTopic = findTopic.get();
        Assertions.assertThat(findUpdatedTopic.getTitle()).isEqualTo("1일 5커밋");
    }

    @Test
    public void 토픽_삭제() throws Exception {
        //given
        TopicCreateRequest topicCreateRequest = getTopicCreateRequest();
        Long savedTopicId = topicService.createTopic(topicCreateRequest);

        //when
        topicService.deleteTopic(savedTopicId);

        //then
        try {
            topicService.getOneTopic(savedTopicId);
        } catch (BusinessException e) {
            org.junit.jupiter.api.Assertions.assertEquals("해당 토픽을 찾을 수 없습니다.", e.getMessage());
        }
    }

    private TopicCreateRequest getTopicCreateRequest() {
        return TopicCreateRequest.builder()
                .title(topic.getTitle())
                .description(topic.getDescription())
                .tags(topic.getTags())
                .pointPerPerson(topic.getPointPerPerson())
                .notice(topic.getNotice())
                .build();
    }
}
