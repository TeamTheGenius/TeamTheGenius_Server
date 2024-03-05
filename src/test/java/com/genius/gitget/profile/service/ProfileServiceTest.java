package com.genius.gitget.profile.service;

import static com.genius.gitget.global.security.constants.ProviderInfo.GITHUB;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.likes.service.LikesService;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.profile.dto.UserInformationResponse;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProfileServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    LikesRepository likesRepository;
    @Autowired
    LikesService likesService;
    @Autowired
    ProfileService profileService;

    static User user1;
    static Topic topic1;
    static Instance instance1, instance2, instance3;

    @BeforeEach
    void setup() {
        user1 = getSavedUser("neo5188@gmail.com", GITHUB, "kimdozzi");

        topic1 = getSavedTopic("1일 1커밋", "BE");

        instance1 = getSavedInstance("1일 1커밋", "BE", 50);
        instance2 = getSavedInstance("1일 1커밋", "BE", 100);
        instance3 = getSavedInstance("1일 1알고리즘", "CS,BE,FE", 500);

        //== 연관관계 ==//
        instance1.setTopic(topic1);
        instance2.setTopic(topic1);
        instance3.setTopic(topic1);

        Likes likes1 = new Likes(user1, instance1);
        Likes likes2 = new Likes(user1, instance2);
        Likes likes3 = new Likes(user1, instance3);
        likesRepository.save(likes1);
        likesRepository.save(likes2);
        likesRepository.save(likes3);
    }

    @Test
    void 유저_조회() {
        UserInformationResponse userInformation = profileService.getUserInformation(user1);
        Assertions.assertThat(userInformation.getIdentifier()).isEqualTo("neo5188@gmail.com");
    }

    @Test
    void 유저_정보_수정() {

    }


    private User getSavedUser(String identifier, ProviderInfo providerInfo, String nickname) {
        User user = userRepository.save(
                User.builder()
                        .identifier(identifier)
                        .providerInfo(providerInfo)
                        .role(Role.ADMIN)
                        .nickname(nickname)
                        .build()
        );
        return user;
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
}
