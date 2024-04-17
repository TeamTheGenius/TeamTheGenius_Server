package com.genius.gitget.challenge.instance.service;


import static org.junit.jupiter.api.Assertions.assertThrows;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstanceDetailResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstancePagingResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateRequest;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.repository.FilesRepository;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class InstanceServiceTest {
    @Autowired
    InstanceService instanceService;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    FilesService filesService;
    @Autowired
    FilesRepository filesRepository;

    private Instance instance, instance1, instance2;
    private Topic topic;
    private Files topicFiles;
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
        instanceService.createInstance(instanceCreateRequest, currentDate);

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
        Long savedInstanceId = instanceService.createInstance(instanceCreateRequest, currentDate);

        InstanceUpdateRequest instanceUpdateRequest = InstanceUpdateRequest.builder()
                .topicId(savedTopic.getId())
                .description("이것은 수정본이지롱")
                .pointPerPerson(instance.getPointPerPerson())
                .startedAt(instance.getStartedDate())
                .completedAt(instance.getCompletedDate())
                .build();

        //when
        Long updatedInstanceId = instanceService.updateInstance(savedInstanceId, instanceUpdateRequest);

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
        Long savedInstanceId = instanceService.createInstance(instanceCreateRequest, currentDate);

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

        org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class, () -> {
            instanceService.getInstanceById(savedInstance1.getId());
        });
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

    private Files getSavedFiles(String originalFilename, String savedFilename, String fileURL, FileType fileType) {
        return filesRepository.save(
                Files.builder()
                        .originalFilename(originalFilename)
                        .savedFilename(savedFilename)
                        .fileURI(fileURL)
                        .fileType(fileType)
                        .build()
        );
    }

    @Nested
    public class 인스턴스_삭제할_때 {
        private Topic topic;
        private Instance instance1, instance2, instance3;

        @BeforeEach
        public void setup() {
            topic = getSavedTopic("1일 1공부", "BE, ML");
            instance1 = getSavedInstance("1일 1공부", "BE, ML", 100);
            instance2 = getSavedInstance("1일 3공부", "BE, ML", 100);
            instance3 = getSavedInstance("1일 3공부", "BE, ML", 100);
            instance1.setTopic(topic);
            instance2.setTopic(topic);
        }

        @Test
        public void 해당_아이디가_존재한다면_삭제할_수_있다() {
            Long id = instance1.getId();
            instanceService.deleteInstance(id);

            assertThrows(BusinessException.class, () -> {
                instanceService.getInstanceById(id);
            });
        }

        @Test
        public void 해당_아이디가_존재하지_않는다면_삭제할_수_없다() {
            Long id = instance3.getId() + 1L;
            assertThrows(BusinessException.class, () -> {
                instanceService.deleteInstance(id);
            });
        }

//        @Test
//        public void 해당_인스턴스에_파일이_존재한다면_같이_삭제한다() {
//            MultipartFile filename = FileTestUtil.getMultipartFile("sky");
//            Files files1 = filesService.uploadFile(filename, FileType.INSTANCE);
//
//            instance1.setFiles(files1);
//            instanceRepository.save(instance1);
//
//            instanceService.deleteInstance(instance1.getId());
//        }
    }
}
