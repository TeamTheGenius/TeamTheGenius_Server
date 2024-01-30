package com.genius.gitget.topic;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.dto.TopicCreateRequest;
import com.genius.gitget.admin.topic.dto.TopicDetailResponse;
import com.genius.gitget.admin.topic.dto.TopicUpdateRequest;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.admin.topic.service.TopicService;
import com.genius.gitget.global.util.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback
public class TopicServiceTest {
    @Autowired
    TopicService topicService;
    @Autowired
    TopicRepository topicRepository;


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
    public void 토픽_생성() throws Exception {
        //given
        TopicCreateRequest topicCreateRequest = new TopicCreateRequest(topic.getTitle(), topic.getDescription(), topic.getTags(), topic.getPointPerPerson());
        Long savedTopicId = topicService.createTopic(topicCreateRequest);

        //when
        TopicDetailResponse topicById = topicService.getTopicById(savedTopicId);

        //then
        Assertions.assertThat(topicById.title()).isEqualTo(topicCreateRequest.title());
    }

    @Test
    public void 토픽_수정() throws Exception {
        //given
        TopicCreateRequest topicCreateRequest = new TopicCreateRequest(topic.getTitle(), topic.getDescription(), topic.getTags(), topic.getPointPerPerson());
        Long savedTopicId = topicService.createTopic(topicCreateRequest);

        //when
        TopicUpdateRequest topicUpdateRequest = new TopicUpdateRequest("1일 5커밋", topic.getDescription(), topic.getTags(), topic.getPointPerPerson());
        topicService.updateTopic(savedTopicId, topicUpdateRequest);

        //then
        Optional<Topic> findTopic = topicRepository.findById(savedTopicId);
        Topic findUpdatedTopic = findTopic.get();
        Assertions.assertThat(findUpdatedTopic.getTitle()).isEqualTo("1일 5커밋");
    }

    @Test
    public void 토픽_삭제() throws Exception {
        //given
        TopicCreateRequest topicCreateRequest = new TopicCreateRequest(topic.getTitle(), topic.getDescription(), topic.getTags(), topic.getPointPerPerson());
        Long savedTopicId = topicService.createTopic(topicCreateRequest);

        //when
        topicService.deleteTopic(savedTopicId);

        //then
        org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class, () -> {
            topicService.getTopicById(1L);
        });
//
//        Assertions.assertThatThrownBy(()-> topicService.getTopicById(1L))
//                .isInstanceOf(BusinessException.class);
//
//        try {
//            topicService.getTopicById(savedTopicId);
//        } catch (BusinessException e) {
//            org.junit.jupiter.api.Assertions.assertEquals("해당 토픽을 찾을 수 없습니다.", e.getMessage());
//        }
    }
}
