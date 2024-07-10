package com.genius.gitget.admin.topic.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

//@ExtendWith(SpringExtension.class)
//@DataJpaTest
@SpringBootTest
@Transactional
@Rollback
@DisplayName("TopicRepository")
public class TopicRepositoryTest {
    Topic topicA, topicB;

    @Autowired
    private TopicRepository topicRepository;

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
    }

    @Nested
    @DisplayName("save 메소드는")
    class Describe_save {

        @BeforeEach
        public void init() {
            topicRepository.deleteAll();
        }

        @Nested
        @DisplayName("토픽 객체가 주어질 때")
        class Context_with_a_topic {

            @Test
//            it_returns_4XX_if_saving_the_obj_fails
            @DisplayName("객체 저장에 성공하면 저장된 객체를 반환합니다.")
            public void it_returns_the_saved_obj_if_saving_an_obj_succeeds() {
                Topic savedTopic = topicRepository.save(topicA);

                assertEquals(topicA.getId(), savedTopic.getId());
                assertEquals(topicA.getTitle(), savedTopic.getTitle());
                assertEquals(topicA.getDescription(), savedTopic.getDescription());
            }
        }
    }


    @Nested
    @DisplayName("search 메소드는")
    class Describe_search {

        @Nested
        @DisplayName("조회 조건에 따라")
        class Context_with_a_topic {

            @BeforeEach
            public void prepare() {
                topicRepository.save(topicA);
                topicRepository.save(topicB);
            }

            @Test
            @DisplayName("토픽 전체를 반환합니다.")
            public void it_returns_topic_obj_list() {
                Page<Topic> topics = topicRepository.findAllById(PageRequest.of(0, 5));
                int topicCount = 0;

                for (Topic topic : topics) {
                    if (topic != null) {
                        topicCount++;
                    }
                }

                assertThat(topicCount).isEqualTo(2);
            }

            @Test
            @DisplayName("특정 토픽을 반환합니다.")
            public void it_returns_topic_obj() {
                Optional<Topic> topic = topicRepository.findById(topicA.getId());

                assertThat(topic.get().getTitle()).isEqualTo("1일 1알고리즘");
            }
        }
    }


    @Nested
    @DisplayName("update 메서드는")
    class Describe_update {

        @Nested
        @DisplayName("토픽 정보를 수정하려고 할 때")
        class Context_with_a_topic {

            @BeforeEach
            public void init() {
                topicRepository.save(topicA);
            }

            @Test
            @DisplayName("생성된 인스턴스가 존재하면 description만 수정할 수 있고, 없다면 모든 항목을 수정할 수 있다.")
            public void it_returns_2XX_if_the_obj_is_updated_successfully() {

                Topic topic = topicRepository.findById(topicA.getId()).orElse(null);

                boolean hasInstance = false;
                if (!topic.getInstanceList().isEmpty()) {
                    hasInstance = true;
                    topic.updateExistInstance("(수정) 하루에 두 문제씩 문제를 해결합니다.");
                } else {
                    topic.updateNotExistInstance("1일 2알고리즘", "(수정) 하루에 두 문제씩 문제를 해결합니다.", "CS", "유의사항", 30000);
                }

                Topic savedTopic = topicRepository.save(topic);

                if (!hasInstance) {
                    assertEquals(topic.getId(), savedTopic.getId());
                    assertEquals("(수정) 하루에 두 문제씩 문제를 해결합니다.", savedTopic.getDescription());
                } else {
                    assertEquals(topic.getId(), savedTopic.getId());
                    assertEquals("(수정) 하루에 두 문제씩 문제를 해결합니다.", savedTopic.getDescription());
                    assertEquals(30000, savedTopic.getPointPerPerson());
                }
            }
        }
    }


    @Nested
    @DisplayName("delete 메서드는")
    class Describe_delete {

        @Nested
        @DisplayName("삭제하려는 토픽 ID가 주어질 때")
        class Context_with_a_topic {

            @BeforeEach
            public void init() {
                topicRepository.save(topicA);
            }

            @Test
            @DisplayName("객체가 성공적으로 삭제되면, 다시 조회할 수 없다.")
            public void it_cannot_be_retrieved_once_an_obj_is_successfully_deleted() {
                Topic topic = topicRepository.findById(topicA.getId()).orElse(null);
                assert topic != null;
                topicRepository.delete(topic);
                Topic findTopic = topicRepository.findById(topicA.getId()).orElse(null);
                Assertions.assertThrows(NullPointerException.class, () -> {
                    findTopic.getId();
                });
            }

            @Test
            @DisplayName("DB에 해당 객체가 없으면, 삭제할 수 없다.")
            public void it_cannot_be_deleted_if_the_obj_does_not_exist() {
                Assertions.assertThrows(Exception.class, () -> {
                    Topic topic = topicRepository.findById(topicB.getId()).orElse(null);
                    topicRepository.delete(topic);
                });
            }
        }
    }
}