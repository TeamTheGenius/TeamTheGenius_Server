package com.genius.todoffin.user.domain;

import com.genius.todoffin.user.repository.UserRepository;
import com.genius.todoffin.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AssertionsKt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static com.genius.todoffin.security.constants.ProviderType.GOOGLE;
import static com.genius.todoffin.security.constants.ProviderType.NAVER;
import static com.genius.todoffin.user.domain.Role.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback(value = false)
    public void 회원_생성_테스트() {
        User user1 = new User(GOOGLE, "neo5188@gmail.com", USER, "김도형", "working out", "hello this is test code.");
        User user2 = new User(NAVER, "ssangcom@naver.com", USER, "성희연", "sing a song", "I'm gonna japan.");

        userRepository.save(user1);
        userRepository.save(user2);

        User findUser1 = userRepository.findByIdentifier("neo5188@gmail.com").get();
        User findUser2 = userRepository.findByIdentifier("ssangcom@naver.com").get();

        assertThat(findUser1.getIdentifier()).isEqualTo(user1.getIdentifier());
        assertThat(findUser2.getIdentifier()).isEqualTo(user2.getIdentifier());

        System.out.println("findUser1 = " + findUser1);
        System.out.println("findUser2 = " + findUser2);
    }
}