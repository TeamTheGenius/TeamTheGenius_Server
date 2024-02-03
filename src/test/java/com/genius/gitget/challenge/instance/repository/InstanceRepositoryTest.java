package com.genius.gitget.challenge.instance.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class InstanceRepositoryTest {
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
        savedInstance.updateInstance("수정되었습니다.", "수정된 유의사항", 10000, LocalDateTime.now(),
                LocalDateTime.now().plusDays(5));

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


    @Test
    @DisplayName("인스턴스들 중, 사용자의 tag가 포함되어 있는 인스턴스들을 반환받을 수 있다.")
    public void should_returnInstances_containsUserTags() {
        //given
        List<String> userTags = List.of("BE", "FE", "AI");
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Direction.DESC, "participantCount"));

        //when
        getSavedInstance("title1", "BE", 10);
        getSavedInstance("title2", "FE", 3);
        getSavedInstance("title3", "FE", 20);
        Slice<Instance> suggestions = instanceRepository.findRecommendations(userTags, Progress.PREACTIVITY,
                pageRequest);

        //then
        assertThat(suggestions.getContent().size()).isEqualTo(3);
        assertThat(suggestions.getContent().get(0).getTitle()).isEqualTo("title3");
        assertThat(suggestions.getContent().get(0).getTags()).isEqualTo("FE");
        assertThat(suggestions.getContent().get(0).getParticipantCount()).isEqualTo(20);

        assertThat(suggestions.getContent().get(1).getTitle()).isEqualTo("title1");
        assertThat(suggestions.getContent().get(1).getTags()).isEqualTo("BE");
        assertThat(suggestions.getContent().get(1).getParticipantCount()).isEqualTo(10);

        assertThat(suggestions.getContent().get(2).getTitle()).isEqualTo("title2");
        assertThat(suggestions.getContent().get(2).getTags()).isEqualTo("FE");
        assertThat(suggestions.getContent().get(2).getParticipantCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("인스턴스들 중, 시작 일자가 늦은 순서대로 인스턴스들을 정렬하여 반환받을 수 있다.")
    public void should_returnInstances_orderByStartedDate() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Direction.DESC, "startedDate"));

        //when
        getSavedInstance("title1", "BE", 10);
        getSavedInstance("title2", "BE", 3);
        getSavedInstance("title3", "BE", 20);
        Slice<Instance> instances = instanceRepository.findInstanceByCondition(Progress.PREACTIVITY, pageRequest);

        //then
        assertThat(instances.getContent().size()).isEqualTo(3);
        assertThat(instances.getContent().get(0).getTitle()).isEqualTo("title3");
        assertThat(instances.getContent().get(0).getTags()).isEqualTo("BE");
        assertThat(instances.getContent().get(0).getParticipantCount()).isEqualTo(20);

        assertThat(instances.getContent().get(1).getTitle()).isEqualTo("title2");
        assertThat(instances.getContent().get(1).getTags()).isEqualTo("BE");
        assertThat(instances.getContent().get(1).getParticipantCount()).isEqualTo(3);

        assertThat(instances.getContent().get(2).getTitle()).isEqualTo("title1");
        assertThat(instances.getContent().get(2).getTags()).isEqualTo("BE");
        assertThat(instances.getContent().get(2).getParticipantCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("인스턴스들 중, 참여 인원 수가 많은 순서대로 인스턴스들을 정렬하여 반환받을 수 있다.")
    public void should_returnInstances_orderByParticipantCnt() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Direction.DESC, "participantCount"));

        //when
        getSavedInstance("title1", "BE", 10);
        getSavedInstance("title2", "BE", 3);
        getSavedInstance("title3", "BE", 20);
        Slice<Instance> instances = instanceRepository.findInstanceByCondition(Progress.PREACTIVITY, pageRequest);

        //then
        assertThat(instances.getContent().size()).isEqualTo(3);
        assertThat(instances.getContent().get(0).getTitle()).isEqualTo("title3");
        assertThat(instances.getContent().get(0).getTags()).isEqualTo("BE");
        assertThat(instances.getContent().get(0).getParticipantCount()).isEqualTo(20);

        assertThat(instances.getContent().get(1).getTitle()).isEqualTo("title1");
        assertThat(instances.getContent().get(1).getTags()).isEqualTo("BE");
        assertThat(instances.getContent().get(1).getParticipantCount()).isEqualTo(10);

        assertThat(instances.getContent().get(2).getTitle()).isEqualTo("title2");
        assertThat(instances.getContent().get(2).getTags()).isEqualTo("BE");
        assertThat(instances.getContent().get(2).getParticipantCount()).isEqualTo(3);
    }

    private Instance getSavedInstance(String title, String tags, int participantCnt) {
        LocalDateTime now = LocalDateTime.now();
        Instance instance = instanceRepository.save(
                Instance.builder()
                        .tags(tags)
                        .title(title)
                        .description("description")
                        .progress(Progress.PREACTIVITY)
                        .pointPerPerson(100)
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        return instance;
    }
}