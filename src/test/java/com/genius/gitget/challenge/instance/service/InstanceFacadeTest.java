package com.genius.gitget.challenge.instance.service;


import static org.junit.jupiter.api.Assertions.assertThrows;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.crud.InstanceDetailResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstancePagingResponse;
import com.genius.gitget.challenge.instance.dto.crud.InstanceUpdateRequest;
import com.genius.gitget.challenge.instance.facade.InstanceFacade;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.instance.util.TestDTOFactory;
import com.genius.gitget.challenge.instance.util.TestSetup;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.repository.FilesRepository;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
public class InstanceFacadeTest {
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    FilesService filesService;
    @Autowired
    FilesRepository filesRepository;
    @Autowired
    InstanceFacade instanceFacade;

    private Instance instanceA;
    private Topic topicA;

    @BeforeEach
    public void setup() {
        List<Topic> topicList = TestSetup.createTopicList();
        List<Instance> instanceList = TestSetup.createInstanceList();

        topicA = topicList.get(0);
        instanceA = instanceList.get(0);

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
                        .startedDate(now.plusDays(5))
                        .completedDate(now.plusDays(10))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        return instance;
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
    @DisplayName("인스턴스 생성 메서드는")
    class Describe_instance_create {
        @Nested
        @DisplayName("instanceCreateRequestDto가 들어오면")
        class Context_with_a_instanceCreateRequestDto {
            @Test
            @DisplayName("인스턴스을 생성한다.")
            public void it_returns_2XX_if_the_instance_was_created_successfully() {
                LocalDate currentDate = instanceA.getStartedDate().minusDays(3).toLocalDate();
                Topic savedTopic = topicRepository.save(topicA);

                InstanceCreateRequest instanceCreateRequest = TestDTOFactory.getInstanceCreateRequest(savedTopic,
                        instanceA);
                instanceFacade.createInstance(instanceCreateRequest, currentDate);

                List<Instance> instanceList = instanceRepository.findAll();
                Assertions.assertThat(instanceList.size()).isEqualTo(1);
            }
        }
    }

    @Nested
    @DisplayName("Instance 수정 메서드는")
    class Describe_instance_modify {

        @Nested
        @DisplayName("InstanceUpdateRequest가 주어지면")
        class Context_with_an_instanceUpdateRequest {

            @Test
            @DisplayName("인스턴스를 수정하고 수정된 내용을 반환한다")
            void it_updates_the_instance_and_returns_the_updated_content() throws Exception {
                LocalDate currentDate = instanceA.getStartedDate().minusDays(3).toLocalDate();
                Topic savedTopic = topicRepository.save(topicA);

                InstanceCreateRequest instanceCreateRequest = TestDTOFactory.getInstanceCreateRequest(savedTopic,
                        instanceA);
                Long savedInstanceId = instanceFacade.createInstance(instanceCreateRequest, currentDate);

                InstanceUpdateRequest instanceUpdateRequest = InstanceUpdateRequest.builder()
                        .topicId(savedTopic.getId())
                        .description("이것은 수정본이지롱")
                        .pointPerPerson(instanceA.getPointPerPerson())
                        .startedAt(instanceA.getStartedDate())
                        .completedAt(instanceA.getCompletedDate())
                        .build();

                Long updatedInstanceId = instanceFacade.modifyInstance(savedInstanceId, instanceUpdateRequest);

                Optional<Instance> byId = instanceRepository.findById(updatedInstanceId);
                Assertions.assertThat(byId.get().getDescription()).isEqualTo("이것은 수정본이지롱");
            }
        }
    }

    @Nested
    @DisplayName("Instance 단건 조회 메서드는")
    class Describe_instance_findOne {

        @Nested
        @DisplayName("Instance ID가 주어졌을 때")
        class Context_with_an_instanceId {

            @Test
            @DisplayName("해당 ID의 인스턴스를 반환한다")
            void it_returns_the_instance_with_the_given_id() throws Exception {
                LocalDate currentDate = instanceA.getStartedDate().minusDays(3).toLocalDate();
                Topic savedTopic = topicRepository.save(topicA);

                InstanceCreateRequest instanceCreateRequest = TestDTOFactory.getInstanceCreateRequest(savedTopic,
                        instanceA);
                Long savedInstanceId = instanceFacade.createInstance(instanceCreateRequest, currentDate);

                //wen
                InstanceDetailResponse instanceById = instanceFacade.findOne(savedInstanceId);

                Assertions.assertThat(instanceById.title()).isEqualTo(instanceCreateRequest.title());
            }
        }
    }

    @Nested
    @DisplayName("Instance 리스트 조회 메서드는")
    class Describe_instance_findAllInstances {

        @Nested
        @DisplayName("페이지 요청이 주어졌을 때")
        class Context_with_a_pageRequest {

            @Test
            @DisplayName("모든 인스턴스를 페이지로 반환한다")
            void it_returns_all_instances_in_a_page() {
                Topic savedTopic = getSavedTopic("1일 1알고리즘", "FE, BE");
                Instance savedInstance1 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
                Instance savedInstance2 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
                Instance savedInstance3 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);

                savedInstance1.setTopic(savedTopic);
                savedInstance2.setTopic(savedTopic);
                savedInstance3.setTopic(savedTopic);

                PageRequest pageRequest = PageRequest.of(0, 10);
                Page<InstancePagingResponse> allInstances = instanceFacade.findAllInstances(pageRequest);

                Assertions.assertThat(allInstances.getTotalElements()).isEqualTo(3);
            }
        }
    }


    @Nested
    @DisplayName("특정 토픽에 대한 인스턴스 리스트 조회 메서드는")
    class Describe_instance_getAllInstancesOfSpecificTopic {
        @Nested
        @DisplayName("특정 토픽 ID가 주어졌을 때")
        class Context_with_a_specificTopicId {

            @Test
            @DisplayName("해당 토픽1의 모든 인스턴스를 페이지로 반환한다")
            void it_returns_all_instances_of_topic1_in_a_page() {
                Topic savedTopic1 = getSavedTopic("1일 1알고리즘", "FE, BE");
                Topic savedTopic2 = getSavedTopic("1일 1알고리즘", "FE, BE");
                Instance savedInstance1 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
                Instance savedInstance2 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
                Instance savedInstance3 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);

                savedInstance1.setTopic(savedTopic1);
                savedInstance2.setTopic(savedTopic1);
                savedInstance3.setTopic(savedTopic2);

                PageRequest pageRequest = PageRequest.of(0, 10);
                Page<InstancePagingResponse> allInstancesOfSpecificTopic = instanceFacade.getAllInstancesOfSpecificTopic(
                        pageRequest, savedTopic1.getId());

                Assertions.assertThat(allInstancesOfSpecificTopic.getTotalElements()).isEqualTo(2);
            }

            @Test
            @DisplayName("해당 토픽2의 모든 인스턴스를 페이지로 반환한다")
            void it_returns_all_instances_of_topic2_in_a_page() {
                Topic savedTopic1 = getSavedTopic("1일 1알고리즘", "FE, BE");
                Topic savedTopic2 = getSavedTopic("1일 1알고리즘", "FE, BE");
                Instance savedInstance1 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
                Instance savedInstance2 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
                Instance savedInstance3 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);

                savedInstance1.setTopic(savedTopic1);
                savedInstance2.setTopic(savedTopic1);
                savedInstance3.setTopic(savedTopic2);

                PageRequest pageRequest = PageRequest.of(0, 10);
                Page<InstancePagingResponse> allInstancesOfSpecificTopic = instanceFacade.getAllInstancesOfSpecificTopic(
                        pageRequest, savedTopic2.getId());

                Assertions.assertThat(allInstancesOfSpecificTopic.getTotalElements()).isEqualTo(1);
            }


            @Test
            @DisplayName("해당 토픽의 모든 인스턴스를 페이지로 반환한다")
            void it_returns_all_instances_of_the_given_topic_in_a_page() {
                Topic savedTopic1 = getSavedTopic("1일 1알고리즘", "FE, BE");
                Topic savedTopic2 = getSavedTopic("1일 1알고리즘", "FE, BE");
                Instance savedInstance1 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
                Instance savedInstance2 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);
                Instance savedInstance3 = getSavedInstance("1일 1알고리즘", "FE, BE", 50);

                savedInstance1.setTopic(savedTopic1);
                savedInstance2.setTopic(savedTopic1);
                savedInstance3.setTopic(savedTopic2);

                PageRequest pageRequest = PageRequest.of(0, 10);
                Page<InstancePagingResponse> allInstancesOfSpecificTopic = instanceFacade.getAllInstancesOfSpecificTopic(
                        pageRequest, savedTopic1.getId());

                Assertions.assertThat(allInstancesOfSpecificTopic.getTotalElements()).isEqualTo(2);
            }
        }
    }

    @Nested
    @DisplayName("Instance 삭제 메서드는")
    class Describe_instance_remove {

        @Nested
        @DisplayName("Instance ID가 주어졌을 때")
        class Context_with_an_instanceId {

            @Test
            @DisplayName("해당 인스턴스를 삭제하고, 삭제된 인스턴스를 조회할 때 예외를 던진다")
            void it_removes_the_instance_and_throws_exception_when_retrieving_deleted_instance() {
                Instance savedInstance = getSavedInstance("1일 1알고리즘", "FE, BE", 50);

                instanceFacade.removeInstance(savedInstance.getId());

                assertThrows(BusinessException.class, () -> instanceFacade.findOne(savedInstance.getId()));
            }
        }

        @Nested
        @DisplayName("인스턴스를 삭제할 때")
        class Context_when_removing_an_instance {
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
            @DisplayName("해당 아이디가 존재한다면 삭제할 수 있다")
            void it_can_remove_if_the_instance_exists() {
                Long id = instance1.getId();

                instanceFacade.removeInstance(id);

                assertThrows(BusinessException.class, () -> {
                    instanceFacade.findOne(id);
                });
            }

            @Test
            @DisplayName("해당 아이디가 존재하지 않는다면 삭제할 수 없다")
            void it_cannot_remove_if_the_instance_does_not_exist() {
                Long id = instance3.getId() + 1L;

                assertThrows(BusinessException.class, () -> {
                    instanceFacade.removeInstance(id);
                });
            }
        }
    }
}