package com.genius.gitget.challenge.user.controller;

import static com.genius.gitget.global.util.exception.ErrorCode.DUPLICATED_NICKNAME;
import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@Slf4j
class UserControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("사용자의 닉네임이 중복된다면 400번대를 반환한다.")
    public void should_return4XX_when_nicknameDuplicated() throws Exception {
        //given
        User user = getSavedUser();

        //when & then
        mockMvc.perform(get("/api/auth/check-nickname?nickname=" + user.getNickname()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.resultCode").value(DUPLICATED_NICKNAME.getStatus().value()))
                .andExpect(jsonPath("$.message").value(DUPLICATED_NICKNAME.getMessage()));
    }

    @Test
    @DisplayName("사용자의 닉네임이 중복되지 않는다면 200번대를 반환한다.")
    public void should_return2XX_when_nicknameNotDuplicated() throws Exception {
        mockMvc.perform(get("/api/auth/check-nickname?nickname=" + "nickname"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.resultCode").value(SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("사용자의 회원가입이 완료되었다면 사용자의 identifier를 전달한다.")
    public void should_passIdentifier_when_signupCompleted() throws Exception {
        //given
        saveUnsignedUser();

        String requestBody = "{\"identifier\" : \"kimdozzi\",\"nickname\" : \"nickname\",\"interest\" : [\"Backend\", \"Frontend\"],\"information\" : \"hello\"}";

        //when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is2xxSuccessful());
    }


    private void saveUnsignedUser() {
        userRepository.save(User.builder()
                .role(Role.NOT_REGISTERED)
                .providerInfo(ProviderInfo.GITHUB)
                .identifier("kimdozzi")
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