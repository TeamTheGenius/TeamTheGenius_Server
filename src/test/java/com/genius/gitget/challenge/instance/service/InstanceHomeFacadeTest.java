package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchRequest;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.facade.InstanceFacade;
import com.genius.gitget.challenge.instance.facade.InstanceHomeFacade;
import com.genius.gitget.challenge.instance.util.TestDTOFactory;
import com.genius.gitget.challenge.instance.util.TestSetup;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
public class InstanceHomeFacadeTest {
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceService instanceService;

    @Autowired
    InstanceFacade instanceFacade;
    @Autowired
    InstanceHomeFacade instanceHomeFacade;

    private Instance instanceA, instanceB, instanceC;
    private Topic topicA;

    @BeforeEach
    public void setup() {
        topicA = TestSetup.createTopicList().get(0);
        instanceA = TestSetup.createInstanceList().get(0);
        instanceB = TestSetup.createInstanceList().get(1);
        instanceC = TestSetup.createInstanceList().get(2);
    }

    @SpringBootTest
    public class InstanceFacadeTest {

        @Nested
        @DisplayName("Instance 검색 메서드는")
        class Describe_instance_search {

            @Nested
            @DisplayName("키워드와 진행 상태가 주어졌을 때")
            class Context_with_keyword_and_progress {

                @Test
                @DisplayName("해당 조건에 맞는 인스턴스를 반환한다")
                void it_returns_instances_matching_the_given_conditions() {
                    Topic savedTopic = topicRepository.save(topicA);
                    LocalDate currentDate = instanceA.getStartedDate().minusDays(3).toLocalDate();

                    instanceFacade.createInstance(TestDTOFactory.getInstanceCreateRequest(savedTopic, instanceA),
                            currentDate);
                    instanceFacade.createInstance(TestDTOFactory.getInstanceCreateRequest(savedTopic, instanceB),
                            currentDate);
                    instanceFacade.createInstance(TestDTOFactory.getInstanceCreateRequest(savedTopic, instanceC),
                            currentDate);

                    int instanceCount = 0;
                    Page<InstanceSearchResponse> orderList = instanceHomeFacade.searchInstancesByKeywordAndProgress(
                            InstanceSearchRequest.builder().keyword("이펙티브").progress("preactivity").build(),
                            PageRequest.of(0, 3));

                    for (InstanceSearchResponse instanceSearchResponse : orderList) {
                        if (instanceSearchResponse.getKeyword() != null) {
                            instanceCount++;
                        }
                    }
                    Assertions.assertThat(instanceCount).isEqualTo(1);
                }
            }
        }
    }
}
