package com.genius.todoffin.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.todoffin.security.constants.ProviderType;
import com.genius.todoffin.user.domain.Role;
import com.genius.todoffin.user.domain.User;
import com.genius.todoffin.user.dto.SignupRequest;
import com.genius.todoffin.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("특정 사용자 가입 테스트")
    public void should_matchValues_when_signupUser() {
        //given
        String email = "test@naver.com";
        saveUnsignedUser();
        SignupRequest signupRequest = SignupRequest.builder()
                .email(email)
                .nickname("nickname")
                .information("information")
                .interest("interest")
                .build();

        //when
        User user = userService.findUserByEmail(email);

        Long signupUserId = userService.signup(signupRequest);
        User foundUser = userService.findUserById(signupUserId);

        //then
        assertThat(user.getEmail()).isEqualTo(foundUser.getEmail());
        assertThat(user.getNickname()).isEqualTo(foundUser.getNickname());
        assertThat(user.getProvider()).isEqualTo(foundUser.getProvider());
        assertThat(user.getInformation()).isEqualTo(foundUser.getInformation());
        assertThat(user.getInterest()).isEqualTo(foundUser.getInterest());
    }


    private void saveUnsignedUser() {
        userRepository.save(User.builder()
                .role(Role.NOT_REGISTERED)
                .provider(ProviderType.NAVER)
                .email("test@naver.com")
                .build());
    }
}