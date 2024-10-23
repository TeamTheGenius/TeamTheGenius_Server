package com.genius.gitget.challenge.likes;

import static com.genius.gitget.challenge.user.domain.Role.ADMIN;
import static com.genius.gitget.challenge.user.domain.Role.USER;
import static com.genius.gitget.global.security.constants.ProviderInfo.GOOGLE;

import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LikesTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    LikesRepository likesRepository;
    @Autowired
    TopicRepository topicRepository;

    private User user1, user2;
    private Instance instance1;
    private Topic topic1;

    @BeforeEach
    public void setup() {
        user1 = User.builder().identifier("neo5188@gmail.com")
                .providerInfo(ProviderInfo.NAVER)
                .nickname("kimdozzi")
                .information("백엔드")
                .tags("운동")
                .role(ADMIN)
                .build();

        user2 = User.builder().identifier("ssang23@naver.com")
                .providerInfo(GOOGLE)
                .nickname("SEONG")
                .information("프론트엔드")
                .tags("영화")
                .role(USER)
                .build();

        instance1 = Instance.builder()
                .title("1일 1커밋")
                .description("챌린지 세부사항입니다.")
                .pointPerPerson(10)
                .tags("BE, CS")
                .progress(Progress.ACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();

        topic1 = Topic.builder()
                .title("1일 1커밋")
                .description("간단한 설명란")
                .pointPerPerson(300)
                .tags("BE, CS")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        topicRepository.save(topic1);
        instance1.setTopic(topic1);
        instanceRepository.save(instance1);
    }

    @Test
    public void 사용자는_챌린지의_인스턴스를_관심목록에_저장한다() {
        Likes like = new Likes(user1, instance1);
        likesRepository.save(like);

        int likeCount = instance1.getLikesList().size();
        Assertions.assertEquals(1, likeCount);

    }
}
