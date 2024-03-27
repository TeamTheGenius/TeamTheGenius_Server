package com.genius.gitget.challenge.instance.service;


import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstanceDetailResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstancePagingResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateRequest;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.util.file.FileTestUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
public class InstanceServiceTest {
    @Autowired
    InstanceService instanceService;

    @Autowired
    InstanceRepository instanceRepository;

    @Autowired
    TopicRepository topicRepository;

    private Instance instance, instance1, instance2;
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
        instanceService.createInstance(instanceCreateRequest,
                FileTestUtil.getMultipartFile("name"), fileType, currentDate);

        //then
        List<Instance> all = instanceRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(1);
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

    @Test
    public void 인스턴스_리스트_조회() {
        Topic savedTopic = getSavedTopic("1일 1알고리즘", "FE, BE");
        Instance savedInstance1 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
        Instance savedInstance2 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
        Instance savedInstance3 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);

        savedInstance1.setTopic(savedTopic);
        savedInstance2.setTopic(savedTopic);
        savedInstance3.setTopic(savedTopic);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<InstancePagingResponse> allInstances = instanceService.getAllInstances(pageRequest);

        Assertions.assertThat(allInstances.getTotalElements()).isEqualTo(3);
    }

    @Test
    public void 특정_토픽에_대한_리스트_조회_1() {
        Topic savedTopic1 = getSavedTopic("1일 1알고리즘", "FE, BE");
        Topic savedTopic2 = getSavedTopic("1일 1알고리즘", "FE, BE");
        Instance savedInstance1 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
        Instance savedInstance2 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
        Instance savedInstance3 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);

        savedInstance1.setTopic(savedTopic1);
        savedInstance2.setTopic(savedTopic1);
        savedInstance3.setTopic(savedTopic2);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<InstancePagingResponse> allInstancesOfSpecificTopic = instanceService.getAllInstancesOfSpecificTopic(
                pageRequest, savedTopic1.getId());

        Assertions.assertThat(allInstancesOfSpecificTopic.getTotalElements()).isEqualTo(2);

    }

    @Test
    public void 특정_토픽에_대한_리스트_조회_2() {
        Topic savedTopic1 = getSavedTopic("1일 1알고리즘", "FE, BE");
        Topic savedTopic2 = getSavedTopic("1일 1알고리즘", "FE, BE");
        Instance savedInstance1 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
        Instance savedInstance2 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
        Instance savedInstance3 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);

        savedInstance1.setTopic(savedTopic1);
        savedInstance2.setTopic(savedTopic1);
        savedInstance3.setTopic(savedTopic2);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<InstancePagingResponse> allInstancesOfSpecificTopic = instanceService.getAllInstancesOfSpecificTopic(
                pageRequest, savedTopic2.getId());

        Assertions.assertThat(allInstancesOfSpecificTopic.getTotalElements()).isEqualTo(1);

    }

    @Test
    public void 인스턴스_삭제() {
        Topic savedTopic1 = getSavedTopic("1일 1알고리즘", "FE, BE");
        Topic savedTopic2 = getSavedTopic("1일 1알고리즘", "FE, BE");
        Instance savedInstance1 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
        Instance savedInstance2 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
        Instance savedInstance3 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);

        instanceService.deleteInstance(savedInstance1.getId());

        InstanceDetailResponse instanceById = instanceService.getInstanceById(savedInstance1.getId());
        
    }

    private Topic getSavedTopic(String title, String tags) {
        Topic topic = topicRepository.save(
                Topic.builder()
                        .title(title)
                        .tags(tags)
                        .description("토픽 설명")
                        .pointPerPerson(100)
                        .build()
        );
        return topic;
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
                        .certificationMethod("인증 방법")
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        return instance;
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
