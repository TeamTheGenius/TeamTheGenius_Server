package com.genius.gitget.instance;

import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
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
public class InstanceSearchRepositoryTest {
    @Autowired
    SearchRepository searchRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceRepository instanceRepository;

    @Test
    public void 인스턴스_검색() throws Exception {
        //given
        Instance instance = Instance.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();
        Instance instance1 = Instance.builder()
                .title("1일 2알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .progress(Progress.DONE)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();
        Instance instance2 = Instance.builder()
                .title("1일 3알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();

        //when
        instanceRepository.save(instance);
        instanceRepository.save(instance1);
        instanceRepository.save(instance2);


        //then
        Page<Instance> order = searchRepository.findByProgressAndTitleContainingOrderByStartedDateDesc( Progress.PREACTIVITY, "고리" , PageRequest.of(0, 3));
        for (Instance item : order) {
            System.out.println("item = " + item);
        }

        Assertions.assertThat(order.getTotalElements()).isEqualTo(2);
    }

}
