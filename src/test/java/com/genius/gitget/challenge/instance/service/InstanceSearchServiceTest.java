package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.facade.InstanceFacade;
import com.genius.gitget.challenge.instance.util.TestDTOFactory;
import com.genius.gitget.challenge.instance.util.TestSetup;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.facade.TopicFacade;
import java.util.List;
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
public class InstanceSearchServiceTest {
    @Autowired
    InstanceFacade instanceFacade;
    @Autowired
    TopicFacade topicFacade;

    /*TODO instanceService Facade 적용 후 제거 예정*/
    @Autowired
    InstanceService instanceService;


    List<Topic> topicList;
    List<Instance> instanceList;
    String fileType;

    @BeforeEach
    public void setup() {
        topicList = TestSetup.createTopicList();
        instanceList = TestSetup.createInstanceList();
        fileType = "topic";
    }

    @Test
    public void 인스턴스_검색() throws Exception {
        List<Topic> topics = topicList;
        Topic topic = topics.get(0);
        int instanceCount = 0;
        topicFacade.create(TestDTOFactory.createTopicCreateRequest(topic.getTitle(),
                topic.getDescription(),
                topic.getTags(),
                topic.getPointPerPerson()));

//
//        Topic savedTopic = topicRepository.save(topic);
//
//        createInstance(savedTopic, instance, instance.getTitle());
//        createInstance(savedTopic, instance, instance.getTitle());
//        createInstance(savedTopic, instance, "title");

        Page<InstanceSearchResponse> orderList = instanceFacade.searchInstances("이펙티브", "preactivity",
                PageRequest.of(0, 3));

        for (InstanceSearchResponse instanceSearchResponse : orderList) {
            if (instanceSearchResponse.getKeyword() != null) {
                instanceCount++;
            }
        }

        Assertions.assertThat(instanceCount).isEqualTo(2);

    }
}
