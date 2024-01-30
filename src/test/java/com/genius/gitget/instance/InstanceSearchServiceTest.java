package com.genius.gitget.instance;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchRequest;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import com.genius.gitget.challenge.instance.service.InstanceSearchService;
import com.genius.gitget.challenge.instance.service.InstanceService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@Rollback
@Slf4j
public class InstanceSearchServiceTest {
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


    @Test
    public void 인스턴스_검색() throws Exception {
        //given
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
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();

        Topic savedTopic = topicRepository.save(topic);

        instanceService.createInstance(new InstanceCreateRequest(savedTopic.getId(), instance.getTitle(), instance.getTags(), instance.getDescription(),
                instance.getPointPerPerson(), instance.getStartedDate(), instance.getCompletedDate()));

        instanceService.createInstance(new InstanceCreateRequest(savedTopic.getId(), instance.getTitle(), instance.getTags(), instance.getDescription(),
                instance.getPointPerPerson(), instance.getStartedDate(), instance.getCompletedDate()));

        instanceService.createInstance(new InstanceCreateRequest(savedTopic.getId(), "테스트", instance.getTags(), instance.getDescription(),
                instance.getPointPerPerson(), instance.getStartedDate(), instance.getCompletedDate()));


        //when
        InstanceSearchRequest instanceSearchRequest = new InstanceSearchRequest("고리", "preactivity");


        //then
        Page<InstanceSearchResponse> orderList = instanceSearchService.searchInstances("고리", "preactivity", PageRequest.of(0, 3));

        for (InstanceSearchResponse instanceSearchResponse : orderList) {
            System.out.println("instanceSearchResponse = " + instanceSearchResponse.keyword());
        }

        Assertions.assertThat(orderList.getTotalElements()).isEqualTo(2);

    }
}
