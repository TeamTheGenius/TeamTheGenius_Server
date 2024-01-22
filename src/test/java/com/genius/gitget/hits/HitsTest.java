package com.genius.gitget.hits;

import com.genius.gitget.hits.domain.Hits;
import com.genius.gitget.hits.repository.HitsRepository;
import com.genius.gitget.instance.domain.Instance;
import com.genius.gitget.instance.domain.Progress;
import com.genius.gitget.instance.repository.InstanceRepository;
import com.genius.gitget.security.constants.ProviderInfo;
import com.genius.gitget.user.domain.User;
import com.genius.gitget.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.genius.gitget.security.constants.ProviderInfo.GOOGLE;
import static com.genius.gitget.security.constants.ProviderType.NAVER;
import static com.genius.gitget.user.domain.Role.ADMIN;
import static com.genius.gitget.user.domain.Role.USER;

@SpringBootTest
@Transactional
@Rollback(value = false)

public class HitsTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    HitsRepository hitsRepository;

    private User user1, user2;
    private Instance instance1;

    @BeforeEach
    public void setup() {
        user1 = User.builder().identifier("neo5188@gmail.com")
                .providerInfo(ProviderInfo.NAVER)
                .nickname("kimdozzi")
                .information("백엔드")
                .interest("운동")
                .role(ADMIN)
                .build();

        user2 = User.builder().identifier("ssang23@naver.com")
                .providerInfo(GOOGLE)
                .nickname("SEONG")
                .information("프론트엔드")
                .interest("영화")
                .role(USER)
                .build();
        instance1 = new Instance("1일 1커밋", "챌린지 세부사항입니다." ,10, "BE, CS",
                100, Progress.ACTIVITY, LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        userRepository.save(user1);
        userRepository.save(user2);
        instanceRepository.save(instance1);
    }

    @Test
    public void 사용자는_챌린지의_인스턴스를_관심목록에_저장한다() {
        Hits like = new Hits(user1, instance1);
        hitsRepository.save(like);

        int likeCount = instance1.getHitsList().size();
        Assertions.assertEquals(1, likeCount);

    }
}