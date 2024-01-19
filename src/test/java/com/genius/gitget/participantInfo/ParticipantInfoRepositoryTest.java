package com.genius.gitget.participantInfo;

import com.genius.gitget.instance.domain.Instance;
import com.genius.gitget.instance.domain.Progress;
import com.genius.gitget.instance.repository.InstanceRepository;
import com.genius.gitget.participantinfo.domain.JoinResult;
import com.genius.gitget.participantinfo.domain.JoinStatus;
import com.genius.gitget.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.participantinfo.repository.ParticipantInfoRepository;

import com.genius.gitget.user.domain.User;
import com.genius.gitget.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.genius.gitget.security.constants.ProviderInfo.GOOGLE;
import static com.genius.gitget.security.constants.ProviderInfo.NAVER;
import static com.genius.gitget.user.domain.Role.ADMIN;
import static com.genius.gitget.user.domain.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class ParticipantInfoRepositoryTest {

    @Autowired
    private ParticipantInfoRepository participantInfoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstanceRepository instanceRepository;


    @Test
    public void 참여자_정보_저장() {
        ParticipantInfo participantInfo = new ParticipantInfo(JoinStatus.YES, JoinResult.SUCCESS);

        ParticipantInfo savedInfo = participantInfoRepository.save(participantInfo);

        Assertions.assertThat(participantInfo.getId()).isEqualTo(savedInfo.getId());
    }

    @Test
    public void 회원과_챌린지_인스턴스_양방향_연관관계_편의_메서드_테스트() {
        // given
        User user1 = userA();
        User user2 = userB();
        userRepository.save(user1);
        userRepository.save(user2);

        // when

        Instance instance1 = instanceA();

        ParticipantInfo participantInfo1 = new ParticipantInfo(JoinStatus.YES, JoinResult.FAIL);
        participantInfo1.setUserAndInstance(user1, instance1);
        participantInfoRepository.save(participantInfo1);

        ParticipantInfo participantInfo2 = new ParticipantInfo(JoinStatus.YES, JoinResult.SUCCESS);
        participantInfo2.setUserAndInstance(user2, instance1);
        participantInfoRepository.save(participantInfo2);

        instanceRepository.save(instance1);

        // then
        assertEquals(1, userRepository.findByIdentifier("neo5188@gmail.com").get().getParticipantInfoList().size());
    }

    private User userA() {
        return User.builder().identifier("neo5188@gmail.com")
                .providerInfo(NAVER)
                .nickname("kimdozzi")
                .information("백엔드")
                .interest("운동")
                .role(ADMIN)
                .build();
    }

    private User userB() {
        return User.builder().identifier("ssang23@naver.com")
                .providerInfo(GOOGLE)
                .nickname("SEONG")
                .information("프론트엔드")
                .interest("영화")
                .role(USER)
                .build();
    }
    private Instance instanceA() {
        return new Instance("1일 1커밋", "챌린지 세부사항입니다." ,10, "BE, CS",
                100, Progress.ACTIVITY, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
    }
}
