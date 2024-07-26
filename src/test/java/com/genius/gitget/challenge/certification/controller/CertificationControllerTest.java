package com.genius.gitget.challenge.certification.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.genius.gitget.challenge.certification.service.CertificationService;
import com.genius.gitget.challenge.certification.service.GithubService;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.util.security.TokenTestUtil;
import com.genius.gitget.util.security.WithMockCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@Transactional
@SpringBootTest
class CertificationControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    TokenTestUtil tokenTestUtil;
    @Autowired
    CertificationService certificationService;
    @Autowired
    GithubService githubService;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    UserRepository userRepository;

    @Value("${github.yeon-personalKey}")
    private String githubToken;

    @Value("${github.yeon-githubId}")
    private String githubId;

    @Value("${github.yeon-repository}")
    private String targetRepo;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Github token을 전달받아서 검증하여 데이터베이스에 저장할 수 있다.")
    @WithMockCustomUser
    public void should_saveToken_when_tokenValid() throws Exception {
        //given
        String requestBody = "{\"githubToken\": \"" + githubToken + "\"}";

        //when

        //then
        mockMvc.perform(post("/api/certification/register/token")
                        .headers(tokenTestUtil.createAccessHeaders())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("가입하지 않은 사용자인 경우 4xx Client error가 발생한다.")
    @WithMockCustomUser(role = Role.NOT_REGISTERED)
    public void should_throwException_when_unregisteredUser() throws Exception {
        mockMvc.perform(post("/api/certification/register/token"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("가입한 사용자이나, JWT가 발급되지 않은 경우 4xx client error가 발생한다.")
    @WithMockCustomUser(role = Role.NOT_REGISTERED)
    public void should_throwException_when_JWTNonExist() throws Exception {
        mockMvc.perform(post("/api/certification/register/token")
                        .headers(tokenTestUtil.createAccessHeaders()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("회원가입 시 사용한 깃허브 계정과 토큰 계정이 같지 않으면 4xx client error가 발생한다.")
    @WithMockCustomUser(identifier = "test")
    public void should_throwException_when_accountIncorrect() throws Exception {
        //given
        String requestBody = "{\"githubToken\": \"" + githubToken + "\"}";

        //when & then
        mockMvc.perform(post("/api/certification/register/token")
                        .headers(tokenTestUtil.createAccessHeaders())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Repository 이름을 전달받아서 검증 후, 데이터베이스에 저장할 수 있다.")
    @WithMockCustomUser
    public void should_saveToken_when_repositoryValid() throws Exception {
        //given
        Instance savedInstance = getSavedInstance();

        //when
        User user = userRepository.findByIdentifier(githubId).get();
        githubService.registerGithubPersonalToken(user, githubToken);

        //then
        mockMvc.perform(get("/api/certification/verify/repository?repo=" + targetRepo)
                        .headers(tokenTestUtil.createAccessHeaders()))
                .andExpect(status().is2xxSuccessful());
    }

    private Instance getSavedInstance() {
        return instanceRepository.save(
                Instance.builder()
                        .progress(Progress.PREACTIVITY)
                        .build()
        );
    }
}