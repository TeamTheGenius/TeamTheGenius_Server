package com.genius.gitget.profile.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.likes.service.LikesService;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
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
public class ProfileControllerTest {
    private static Topic savedTopic1, savedTopic2;
    private static Instance savedInstance1, savedInstance2;
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    TokenTestUtil tokenTestUtil;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    FilesService filesService;
    @Autowired
    LikesService likesService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LikesRepository likesRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        savedTopic1 = getSavedTopic();
        savedTopic2 = getSavedTopic();

        savedInstance1 = getSavedInstance("title1", "FE", 50, 1000);
        savedInstance2 = getSavedInstance("title2", "BE, CS", 50, 1000);

        savedInstance1.setTopic(savedTopic1);
        savedInstance2.setTopic(savedTopic1);
    }

    // 사용자 상세 정보 조회
    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 상세 정보 조회에 성공하면, 상태 코드 200을 반환한다.")
    public void 사용자_상세_정보_조회_성공() throws Exception {

        mockMvc.perform(get("/api/profile").cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 상세 정보 조회 시 같은 사용자 정보가 있으면 실패하고, 4xx(IncorrectResultSizeDataAccessException)를 반환한다.")
    public void 사용자_상세_정보_조회_실패() throws Exception {
        User user = getSavedUser();
        mockMvc.perform(get("/api/profile").cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    // 사용자 정보 조회
    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 정보 조회에 성공하면, 상태 코드 200을 반환한다.")
    public void 사용자_정보_조회_성공() throws Exception {
        List<User> users = userRepository.findAllByIdentifier("kimdozzi");
        Long id = null;
        for (User user : users) {
            if (user.getIdentifier().equals("kimdozzi")) {
                id = user.getId();
            }
        }
        Map<String, Long> input = new HashMap<>();
        input.put("userId", id);

        mockMvc.perform(post("/api/profile")
                        .cookie(tokenTestUtil.createAccessCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 정보 조회에 실패하면, 상태 코드 4xx을 반환한다.")
    public void 사용자_정보_조회_실패() throws Exception {
        List<User> users = userRepository.findAllByIdentifier("kimdozzi");
        Long id = null;
        for (User user : users) {
            if (user.getIdentifier().equals("kimdozzi")) {
                id = user.getId();
            }
        }
        Map<String, Long> input = new HashMap<>();
        input.put("userId", id + 1);

        mockMvc.perform(post("/api/profile")
                        .cookie(tokenTestUtil.createAccessCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    // 관심사 조회
    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 관심사 조회에 성공하면, 상태 코드 200을 반환한다.")
    public void 사용자_관심사_조회_성공() throws Exception {
        mockMvc.perform(get("/api/profile/interest").cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // 관심사 수정
    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 관심사 수정에 성공하면, 상태 코드 200을 반환한다.")
    public void 사용자_관심사_수정_성공() throws Exception {

        Map<String, List<String>> input = new HashMap<>();
        input.put("tags", new ArrayList<>(Arrays.asList("FE", "BE", "ML")));

        mockMvc.perform(post("/api/profile/interest").cookie(tokenTestUtil.createAccessCookie())
                        .content(objectMapper.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        User findUser = null;
        List<User> users = userRepository.findAllByIdentifier("kimdozzi");
        for (User user : users) {
            if (user.getIdentifier().equals("kimdozzi")) {
                findUser = user;
            }
        }

        Assertions.assertThat(findUser.getTags()).isEqualTo(String.join(",", findUser.getTags()));
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 관심사 수정에 실패하면, 상태 코드 4xx을 반환한다.")
    public void 사용자_관심사_수정_실패_1() throws Exception {

        Map<String, List<String>> input = new HashMap<>();
        input.put("tags", new ArrayList<>(Arrays.asList("FE", "BE", "ML")));

        mockMvc.perform(post("/api/profile/interest").cookie(tokenTestUtil.createAccessCookie())
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 관심사 수정에 실패하면, 상태 코드 4xx을 반환한다.")
    public void 사용자_관심사_수정_실패_2() throws Exception {

        mockMvc.perform(post("/api/profile/interest").cookie(tokenTestUtil.createAccessCookie())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    // 챌린지 현황
    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 챌린지 현황 조회에 성공하면, 상태 코드 200을 반환한다.")
    public void 사용자_챌린지_현황_성공() throws Exception {
        mockMvc.perform(get("/api/profile/challenges").cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // 탈퇴하기
    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 탈퇴에 성공하면, 상태 코드 200을 반환한다.")
    public void 사용자_탈퇴_성공() throws Exception {

        Map<String, String> input = new HashMap<>();
        input.put("reason", "이용이 불편해서");

        mockMvc.perform(delete("/api/profile").cookie(tokenTestUtil.createAccessCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 탈퇴 사유없이 탈퇴를 요청하면 실패하고, 상태 코드 4xx을 반환한다.")
    public void 사용자_탈퇴_실패() throws Exception {
        mockMvc.perform(delete("/api/profile").cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    // 포인트 조회
    @Test
    @WithMockCustomUser(identifier = "kimdozzi")
    @DisplayName("사용자 포인트 조회에 성공하면, 상태 코드 200을 반환한다.")
    public void 사용자_포인트_조회_성공() throws Exception {
        mockMvc.perform(get("/api/profile/point").cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isOk());
    }


    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname1")
                        .tags("FE, BE")
                        .providerInfo(ProviderInfo.GITHUB)
                        .information("info")
                        .identifier("kimdozzi")
                        .build()
        );
    }

    private Topic getSavedTopic() {
        Topic topic = topicRepository.save(
                Topic.builder()
                        .title("title")
                        .notice("notice")
                        .description("description")
                        .tags("BE")
                        .pointPerPerson(100)
                        .build()
        );

        return topic;
    }

    private Instance getSavedInstance(String title, String tags, int participantCnt, int pointPerPerson) {
        LocalDateTime now = LocalDateTime.now();
        Instance instance = instanceRepository.save(
                Instance.builder()
                        .tags(tags)
                        .title(title)
                        .description("description")
                        .progress(Progress.PREACTIVITY)
                        .pointPerPerson(pointPerPerson)
                        .certificationMethod("인증 방법")
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        return instance;
    }
}
