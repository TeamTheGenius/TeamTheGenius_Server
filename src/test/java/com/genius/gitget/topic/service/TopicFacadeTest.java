package com.genius.gitget.topic.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.dto.TopicCreateRequest;
import com.genius.gitget.topic.dto.TopicDetailResponse;
import com.genius.gitget.topic.dto.TopicUpdateRequest;
import com.genius.gitget.topic.facade.TopicFacade;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class TopicFacadeTest {
    Topic topicA, topicB;
    String fileType;

    @Autowired
    TopicFacade topicFacade;

    @BeforeEach
    public void setup() {
        topicA = Topic.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .build();

        topicB = Topic.builder()
                .title("1일 2알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(300)
                .build();

        fileType = "topic";
    }

    private TopicCreateRequest getTopicCreateRequest() {
        return TopicCreateRequest.builder()
                .title(topicA.getTitle())
                .description(topicA.getDescription())
                .tags(topicA.getTags())
                .pointPerPerson(topicA.getPointPerPerson())
                .notice(topicA.getNotice())
                .build();
    }

    private TopicUpdateRequest getTopicUpdateRequest(String title, String description, String tags, int pointPerPersion,
                                                     String notice) {
        return TopicUpdateRequest.builder()
                .title(title)
                .description(description)
                .tags(tags)
                .pointPerPerson(pointPerPersion)
                .notice(notice).build();
    }

    @Nested
    @DisplayName("토픽 생성 메서드는")
    class Describe_topic_create {

        @Nested
        @DisplayName("topicCreateRequestDto가 들어오면")
        class Context_with_a_topicCreateRequestDto {

            @Test
            @DisplayName("토픽을 생성한다.")
            public void it_returns_2XX_if_the_topic_was_created_successfully() {
                TopicCreateRequest topicCreateRequest = getTopicCreateRequest();

                Long savedTopicId = topicFacade.create(topicCreateRequest);

                TopicDetailResponse topicById = topicFacade.findOne(savedTopicId);

                Assertions.assertThat(topicById.title()).isEqualTo(topicCreateRequest.title());
            }
        }
    }

    @Nested
    @DisplayName("토픽 수정 메서드는")
    class Describe_topic_update {

        @Nested
        @DisplayName("TopicUpdateRequestDto가 들어오면")
        class Context_with_a_TopicUpdateRequestDto {

            @Test
            @DisplayName("토픽 내용을 수정한다.")
            public void it_returns_2XX_if_the_topic_is_modified() {
                TopicCreateRequest topicCreateRequest = getTopicCreateRequest();
                Long savedTopicId = topicFacade.create(topicCreateRequest);

                TopicUpdateRequest topicUpdateRequest = getTopicUpdateRequest("1일 5커밋", topicA.getDescription(),
                        topicA.getTags(), topicA.getPointPerPerson(), topicA.getNotice());

                topicFacade.update(savedTopicId, topicUpdateRequest);

                TopicDetailResponse findTopic = topicFacade.findOne(savedTopicId);
                Assertions.assertThat(findTopic.title()).isEqualTo("1일 5커밋");
            }
        }
    }

    @Nested
    @DisplayName("토픽 삭제 메서드는")
    class Describe_topic_delete {

        @Nested
        @DisplayName("삭제할 토픽 Id가 주어질 때")
        class Context_with_a_TopicUpdateRequestDto {

            @Test
            @DisplayName("해당 토픽을 삭제한다.")
            public void it_returns_2XX_if_the_topic_is_successfully_deleted() throws Exception {
                TopicCreateRequest topicCreateRequest = getTopicCreateRequest();
                Long savedTopicId = topicFacade.create(topicCreateRequest);

                topicFacade.delete(savedTopicId);

                try {
                    topicFacade.findOne(savedTopicId);
                } catch (BusinessException e) {
                    assertEquals("해당 토픽을 찾을 수 없습니다.", e.getMessage());
                }
            }
        }
    }
}
