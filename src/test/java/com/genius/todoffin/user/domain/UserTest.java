package com.genius.todoffin.user.domain;

import com.genius.todoffin.user.repository.UserRepository;
import com.genius.todoffin.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AssertionsKt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static com.genius.todoffin.security.constants.ProviderType.*;
import static com.genius.todoffin.user.domain.Role.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xmlunit.util.Linqy.count;

@SpringBootTest
@Transactional
public class UserTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 사용자_추가() {
        User user = User.builder().identifier("neo5188@gmail.com")
                .providerInfo(NAVER)
                .nickname("kimdozzi")
                .information("백엔드")
                .interest("운동")
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
        for (User user : users) {
            System.out.println("user = " + user);
        }
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

}