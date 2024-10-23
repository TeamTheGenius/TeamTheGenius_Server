package com.genius.gitget.challenge.likes.service;

import static com.genius.gitget.global.security.constants.ProviderInfo.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.dto.UserLikesResponse;
import com.genius.gitget.challenge.likes.facade.LikesFacade;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LikesFacadeTest {
    private User userA;
    private Topic topicA;
    private Instance instanceA, instanceB, instanceC;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private LikesFacade likesFacade;


    @BeforeEach
    void setup() {
        userA = saveUser("test@gmail.com", GITHUB, "kimdozzi");

        topicA = saveTopic("1일 1커밋", "BE");

        instanceA = saveInstance("1일 1커밋", "BE", 50, 100);
        instanceB = saveInstance("1일 1커밋", "BE", 50, 150);
        instanceC = saveInstance("1일 1알고리즘", "CS,BE,FE", 50, 200);

        associateInstancesWithTopic(topicA, instanceA, instanceB, instanceC);

        saveLikes(userA, instanceA, instanceB, instanceC);
    }

    @Nested
    @DisplayName("좋아요 추가 메서드는")
    class Describe_add_likes {
        @Nested
        @DisplayName("유저와 인스턴스가 주어지면")
        class Context_with_a_user_and_instance {
            @Test
            @DisplayName("유저의 좋아요 목록에 추가된다")
            void it_adds_the_instance_to_user_likes() {
                likesFacade.addLikes(userA, "test@gmail.com", instanceA.getId());

                List<Likes> allLikes = likesRepository.findAll();
                long count = allLikes.stream()
                        .filter(like -> like.getUser().getIdentifier().equals("test@gmail.com") && like.getInstance()
                                .getTitle()
                                .equals("1일 1커밋")).count();

                assertThat(count).isEqualTo(allLikes.size() - 1);
            }
        }
    }

    @Nested
    @DisplayName("좋아요 삭제 메서드는")
    class Describe_delete_likes {
        @Nested
        @DisplayName("유저와 좋아요 ID가 주어지면")
        class Context_with_a_user_and_likes_id {
            @Test
            @DisplayName("유저의 좋아요 목록에서 삭제된다")
            void it_removes_the_instance_from_user_likes() {
                List<Likes> likesList = likesRepository.findAll();
                Long likesId = likesList.get(0).getId();

                likesFacade.deleteLikes(userA, likesId);

                assertThrows(BusinessException.class,
                        () -> likesRepository.findById(likesId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.LIKES_NOT_FOUND)));

                List<Likes> allLikes = likesRepository.findAll();

                assertThat(allLikes.size()).isEqualTo(2);
            }
        }
    }

    @Nested
    @DisplayName("유저의 좋아요 목록 조회 메서드는")
    class Describe_get_likes_list {

        @Nested
        @DisplayName("모든 좋아요 목록을 조회할 때")
        class Context_when_retrieving_all_likes {
            @Test
            @DisplayName("유저의 좋아요 목록을 반환한다")
            void it_returns_all_likes() {
                List<Likes> allLikes = likesRepository.findAll();

                for (int i = 0; i < allLikes.size(); i++) {
                    if (i <= 1) {
                        assertThat(allLikes.get(i).getInstance().getTitle()).isEqualTo("1일 1커밋");
                    } else {
                        assertThat(allLikes.get(i).getInstance().getTitle()).isEqualTo("1일 1알고리즘");
                    }
                }
                assertThat(allLikes.size()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("페이징된 좋아요 목록을 조회할 때")
        class Context_when_retrieving_paginated_likes {
            @Test
            @DisplayName("유저의 페이징된 좋아요 목록을 반환한다")
            void it_returns_paginated_likes() {
                PageRequest pageRequest = PageRequest.of(0, 5);
                Page<UserLikesResponse> likesResponses = likesFacade.getLikesList(userA, pageRequest);

                assertThat(likesResponses.getContent().size()).isEqualTo(3);
                assertThat(likesResponses.getContent().get(2).getTitle()).isEqualTo("1일 1커밋");
                assertThat(likesResponses.getContent().get(2).getPointPerPerson()).isEqualTo(100);
                assertThat(likesResponses.getContent().get(1).getTitle()).isEqualTo("1일 1커밋");
                assertThat(likesResponses.getContent().get(1).getPointPerPerson()).isEqualTo(150);
                assertThat(likesResponses.getContent().get(0).getTitle()).isEqualTo("1일 1알고리즘");
                assertThat(likesResponses.getContent().get(0).getPointPerPerson()).isEqualTo(200);
            }
        }
    }

    private User saveUser(String identifier, ProviderInfo providerInfo, String nickname) {
        return userRepository.save(
                User.builder()
                        .identifier(identifier)
                        .providerInfo(providerInfo)
                        .role(Role.ADMIN)
                        .nickname(nickname)
                        .build()
        );
    }

    private Topic saveTopic(String title, String tags) {
        return topicRepository.save(
                Topic.builder()
                        .title(title)
                        .tags(tags)
                        .description("토픽 설명")
                        .pointPerPerson(100)
                        .build()
        );
    }

    private Instance saveInstance(String title, String tags, int participantCnt, int pointPerPerson) {
        LocalDateTime now = LocalDateTime.now();
        Instance instance = instanceRepository.save(
                Instance.builder()
                        .tags(tags)
                        .title(title)
                        .description("description")
                        .progress(Progress.PREACTIVITY)
                        .pointPerPerson(pointPerPerson)
                        .certificationMethod("인증 방법")
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        return instance;
    }


    private void associateInstancesWithTopic(Topic topic, Instance... instances) {
        for (Instance instance : instances) {
            instance.setTopic(topic);
        }
    }

    private void saveLikes(User user, Instance... instances) {
        for (Instance instance : instances) {
            likesRepository.save(new Likes(user, instance));
        }
    }
}