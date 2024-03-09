package com.genius.gitget.challenge.instance.service;


import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstanceDetailResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateRequest;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.util.file.FileTestUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
public class InstanceServiceTest {
    @Autowired
    InstanceService instanceService;

    @Autowired
    InstanceRepository instanceRepository;

    @Autowired
    TopicRepository topicRepository;

    private Instance instance;
    private Topic topic;
    private String fileType;

    @BeforeEach
    public void setup() {
        instance = Instance.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();
        topic = Topic.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(100)
                .build();
        fileType = "instance";
    }

    @Test
    public void 인스턴스_생성() throws Exception {
        //given
        LocalDate currentDate = instance.getCompletedDate().minusDays(3).toLocalDate();
        Topic savedTopic = topicRepository.save(topic);
        InstanceCreateRequest instanceCreateRequest = getInstanceCreateRequest(savedTopic, instance);

        //when
        Long savedInstanceId = instanceService.createInstance(instanceCreateRequest,
                FileTestUtil.getMultipartFile("name"), fileType, currentDate);

        //then
        Optional<Instance> byId = instanceRepository.findById(savedInstanceId);
        Assertions.assertThat(byId.get().getId()).isEqualTo(savedInstanceId);
    }

    @Test
    public void 인스턴스_수정() throws Exception {
        //given
        LocalDate currentDate = instance.getCompletedDate().minusDays(3).toLocalDate();
        Topic savedTopic = topicRepository.save(topic);

        InstanceCreateRequest instanceCreateRequest = getInstanceCreateRequest(savedTopic, instance);
        Long savedInstanceId = instanceService.createInstance(instanceCreateRequest,
                FileTestUtil.getMultipartFile("name"), fileType, currentDate);

        InstanceUpdateRequest instanceUpdateRequest = InstanceUpdateRequest.builder()
                .topicId(savedTopic.getId())
                .description("이것은 수정본이지롱")
                .pointPerPerson(instance.getPointPerPerson())
                .startedAt(instance.getStartedDate())
                .completedAt(instance.getCompletedDate())
                .build();

        //when
        Long updatedInstanceId = instanceService.updateInstance(savedInstanceId, instanceUpdateRequest,
                FileTestUtil.getMultipartFile("name"), fileType);

        //then
        Optional<Instance> byId = instanceRepository.findById(updatedInstanceId);
        Assertions.assertThat(byId.get().getDescription()).isEqualTo("이것은 수정본이지롱");
    }

    @Test
    public void 인스턴스_단건_조회() throws Exception {
        //given
        LocalDate currentDate = instance.getCompletedDate().minusDays(3).toLocalDate();
        Topic savedTopic = topicRepository.save(topic);

        InstanceCreateRequest instanceCreateRequest = getInstanceCreateRequest(savedTopic, instance);
        Long savedInstanceId = instanceService.createInstance(instanceCreateRequest,
                FileTestUtil.getMultipartFile("name"), fileType, currentDate);

        //when
        InstanceDetailResponse instanceById = instanceService.getInstanceById(savedInstanceId);

        //then
        Assertions.assertThat(instanceById.title()).isEqualTo(instanceCreateRequest.title());
    }

    private InstanceCreateRequest getInstanceCreateRequest(Topic savedTopic, Instance instance) {
        return InstanceCreateRequest.builder()
                .topicId(savedTopic.getId())
                .title(instance.getTitle())
                .tags(instance.getTags())
                .description(instance.getDescription())
                .notice(instance.getNotice())
                .pointPerPerson(instance.getPointPerPerson())
                .startedAt(instance.getStartedDate())
                .completedAt(instance.getCompletedDate())
                .build();
    }
}
