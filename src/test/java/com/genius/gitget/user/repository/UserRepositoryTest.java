package com.genius.gitget.user.repository;

import static com.genius.gitget.global.security.constants.ProviderInfo.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("email을 통해 저장한 User 객체 찾은 후, 검증")
    public void email을_통해_저장한_User_객체를_찾을수있다() {
        //given
        String email = "test@naver.com";
        ProviderInfo providerInfo = ProviderInfo.GOOGLE;
        String nickname = "test_nickname";
        User user = getUnsavedUser(email, providerInfo, nickname);

        //when
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findByIdentifier(email).get();

        //then
        assertThat(savedUser.getId()).isEqualTo(foundUser.getId());
        assertThat(savedUser.getIdentifier()).isEqualTo(foundUser.getIdentifier());
        assertThat(savedUser.getProviderInfo()).isEqualTo(foundUser.getProviderInfo());
        assertThat(savedUser.getNickname()).isEqualTo(foundUser.getNickname());
    }

    @Test
    @DisplayName("User 객체 저장 테스트")
    public void email_provider를_통해_저장한_User_객체를_찾을수있다() {
        //given
        String email = "test@naver.com";
        ProviderInfo providerInfo = ProviderInfo.GOOGLE;
        String nickname = "test_nickname";
        User user = getUnsavedUser(email, providerInfo, nickname);

        //when
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findByOAuthInfo(email, providerInfo).get();

        //then
        assertThat(savedUser.getId()).isEqualTo(foundUser.getId());
        assertThat(savedUser.getIdentifier()).isEqualTo(foundUser.getIdentifier());
        assertThat(savedUser.getProviderInfo()).isEqualTo(foundUser.getProviderInfo());
        assertThat(savedUser.getNickname()).isEqualTo(foundUser.getNickname());
    }

    @Test
    @DisplayName("User nickname 중복 방지 테스트")
    public void checkNicknameDuplicate() {
        //given
        User user1 = getUnsavedUser("SSung023", GITHUB, "nickname");

        //when
        User savedUser = userRepository.save(user1);
        User nickname = userRepository.findByNickname("nickname").get();

        //then
        assertThat(nickname).isEqualTo(savedUser);
    }


    private User getUnsavedUser(String identifier, ProviderInfo providerInfo, String nickname) {
        return User.builder()
                .identifier(identifier)
                .providerInfo(providerInfo)
                .role(Role.USER)
                .nickname(nickname)
                .build();
    }
}