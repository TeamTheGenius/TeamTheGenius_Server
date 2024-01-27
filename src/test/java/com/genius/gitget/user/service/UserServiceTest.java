package com.genius.gitget.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.dto.SignupRequest;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.util.List;
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
    @DisplayName("특정 사용자를 가입한 이후, 사용자를 찾았을 때 가입했을 때 입력한 정보와 일치해야 한다.")
    public void should_matchValues_when_signupUser() {
        //given
        String email = "test@naver.com";
        saveUnsignedUser();
        SignupRequest signupRequest = SignupRequest.builder()
                .identifier(email)
                .nickname("nickname")
                .information("information")
                .interest(List.of("관심사1", "관심사2"))
                .build();

        //when
        User user = userService.findUserByIdentifier(email);

        Long signupUserId = userService.signup(signupRequest);
        User foundUser = userService.findUserById(signupUserId);
        //then
        assertThat(user.getIdentifier()).isEqualTo(foundUser.getIdentifier());
        assertThat(user.getNickname()).isEqualTo(foundUser.getNickname());
        assertThat(user.getProviderInfo()).isEqualTo(foundUser.getProviderInfo());
        assertThat(user.getInformation()).isEqualTo(foundUser.getInformation());
        assertThat(user.getTags()).isEqualTo(foundUser.getTags());
    }

    @Test
    @DisplayName("저장되어 있는 사용자를 PK를 통해 찾을 수 있다.")
    public void should_returnUser_when_passPK() {
        //given
        User user = getSavedUser();

        //when
        User foundUser = userService.findUserById(user.getId());

        //then
        assertThat(user.getId()).isEqualTo(foundUser.getId());
        assertThat(user.getIdentifier()).isEqualTo(foundUser.getIdentifier());
        assertThat(user.getProviderInfo()).isEqualTo(foundUser.getProviderInfo());
        assertThat(user.getNickname()).isEqualTo(foundUser.getNickname());
        assertThat(user.getRole()).isEqualTo(foundUser.getRole());
        assertThat(user.getInformation()).isEqualTo(foundUser.getInformation());
        assertThat(user.getTags()).isEqualTo(foundUser.getTags());
    }

    @Test
    @DisplayName("저장되어 있는 사용자를 identifier를 통해 찾을 수 있다.")
    public void should_returnUser_when_passIdentifier() {
        //given
        User user = getSavedUser();

        //when
        User foundUser = userService.findUserByIdentifier(user.getIdentifier());

        //then
        assertThat(user.getId()).isEqualTo(foundUser.getId());
        assertThat(user.getIdentifier()).isEqualTo(foundUser.getIdentifier());
        assertThat(user.getProviderInfo()).isEqualTo(foundUser.getProviderInfo());
        assertThat(user.getNickname()).isEqualTo(foundUser.getNickname());
        assertThat(user.getRole()).isEqualTo(foundUser.getRole());
        assertThat(user.getInformation()).isEqualTo(foundUser.getInformation());
        assertThat(user.getTags()).isEqualTo(foundUser.getTags());
    }

    @Test
    @DisplayName("이미 등록되어 있는 닉네임인 경우 예외가 발생한다.")
    public void should_throwException_when_nicknameIsDuplicated() {
        //given
        User user = getSavedUser();

        //when & then
        assertThatThrownBy(() -> userService.isNicknameDuplicate(user.getNickname()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DUPLICATED_NICKNAME.getMessage());
    }
    

    private void saveUnsignedUser() {
        userRepository.save(User.builder()
                .role(Role.NOT_REGISTERED)
                .providerInfo(ProviderInfo.NAVER)
                .identifier("test@naver.com")
                .build());
    }

    private User getSavedUser() {
        return userRepository.save(User.builder()
                .identifier("identifier")
                .role(Role.USER)
                .information("information")
                .tags("interest1,interest2")
                .nickname("nickname")
                .providerInfo(ProviderInfo.GITHUB)
                .build());
    }
}