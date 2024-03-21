package com.genius.gitget.challenge.user.domain;

import static com.genius.gitget.challenge.user.domain.Role.ADMIN;
import static com.genius.gitget.challenge.user.domain.Role.USER;
import static com.genius.gitget.global.security.constants.ProviderInfo.GOOGLE;
import static com.genius.gitget.global.security.constants.ProviderInfo.NAVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.xmlunit.util.Linqy.count;

import com.genius.gitget.challenge.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 사용자_추가() {
        User user = User.builder().identifier("neo5188@gmail.com")
                .providerInfo(NAVER)
                .nickname("kimdozzi")
                .information("백엔드")
                .tags("운동")
                .role(ADMIN)
                .build();

        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isEqualTo(user.getId());
    }

    @Test
    public void 사용자_목록_조회() {
        User savedUser1 = userRepository.save(userA());
        User savedUser2 = userRepository.save(userB());

        List<User> users = userRepository.findAll();
        assertThat(count(users)).isEqualTo(2);
        assertThat(savedUser1).isNotSameAs(savedUser2);
    }

    @Test
    public void 사용자_정보_수정() {
        User savedUser1 = userRepository.save(userA());

        String nickName = "zzanggu";
        String information = "This is updated info !!";
        String interest = "This is interest!!";

        savedUser1.updateUser(nickName, information, interest);
        User savedUser = userRepository.save(savedUser1);

        assertThat(nickName).isEqualTo(savedUser.getNickname());

        System.out.println("savedUser.getNickname() = " + savedUser.getNickname());
        System.out.println("savedUser.getIdentifier() = " + savedUser.getIdentifier());

    }

    @Test
    public void 사용자_삭제() {
        User savedUser1 = userRepository.save(userA());
        User savedUser2 = userRepository.save(userB());

        userRepository.deleteAll();

        List<User> users = userRepository.findAll();

        assertThat(users.size()).isEqualTo(0);
    }

    private User userA() {
        return User.builder().identifier("neo5188@gmail.com")
                .providerInfo(NAVER)
                .nickname("kimdozzi")
                .information("백엔드")
                .tags("운동")
                .role(ADMIN)
                .build();
    }

    private User userB() {
        return User.builder().identifier("ssang23@naver.com")
                .providerInfo(GOOGLE)
                .nickname("SEONG")
                .information("프론트엔드")
                .tags("영화")
                .role(USER)
                .build();
    }
}