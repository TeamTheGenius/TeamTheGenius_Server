package com.genius.gitget.instance;

import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
@Slf4j
public class InstanceRepositoryTest {
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceRepository instanceRepository;

    @Test
    public void 인스턴스_생성() throws Exception {
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

        //when
        Instance savedInstance = instanceRepository.save(instance);

        //then
        Assertions.assertThat(savedInstance.getTitle()).isEqualTo("1일 1알고리즘");
    }

    @Test
    public void 인스턴스_수정() throws Exception {
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

        //when
        Instance savedInstance = instanceRepository.save(instance);
        savedInstance.updateInstance("수정되었습니다.", 10000, LocalDateTime.now(), LocalDateTime.now().plusDays(5));

        //then
        Assertions.assertThat(instance.getDescription()).isEqualTo(savedInstance.getDescription());
    }

    @Test
    public void 인스턴스_조회() throws Exception {
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

        //when
        Instance savedInstance = instanceRepository.save(instance);

        //then
        Assertions.assertThat(savedInstance.getTitle()).isEqualTo("1일 1알고리즘");
    }

    @Test
    public void 인스턴스_리스트_조회() throws Exception {
        //given
        Instance instance1 = Instance.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();

        Instance instance2 = Instance.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();

        //when
        instanceRepository.save(instance1);
        instanceRepository.save(instance2);

        Page<Instance> instances = instanceRepository.findAllById(PageRequest.of(0, 5, Sort.Direction.DESC, "id"));

        for (Instance instance : instances) {
            if (instance != null) {
                System.out.println("instance = " + instance.getId() + " " + instance.getTitle());
            }
        }

        //then
        Assertions.assertThat(instances.getTotalElements()).isEqualTo(2);
    }
}
