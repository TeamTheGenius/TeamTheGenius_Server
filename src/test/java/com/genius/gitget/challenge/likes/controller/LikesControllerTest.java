package com.genius.gitget.challenge.likes.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.dto.UserLikesAddRequest;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.likes.service.LikesService;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.service.FilesManager;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import com.genius.gitget.util.security.TokenTestUtil;
import com.genius.gitget.util.security.WithMockCustomUser;
import java.time.LocalDateTime;
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
public class LikesControllerTest {
    private static Topic savedTopic1;
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
    FilesManager filesManager;
    @Autowired
    LikesService likesService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LikesRepository likesRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        savedTopic1 = getSavedTopic();

        savedInstance1 = getSavedInstance("title1", "FE", 50, 1000);
        savedInstance2 = getSavedInstance("title2", "BE, CS", 50, 1000);

        savedInstance1.setTopic(savedTopic1);
        savedInstance2.setTopic(savedTopic1);
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi", role = Role.ADMIN)
    @DisplayName("유저가 좋아요 목록을 조회하면, 상태 코드 200을 반환한다.")
    public void 좋아요_목록_조회_성공_1() throws Exception {
        User user = getSavedUser();
//        likesRepository.save(Likes.builder()
//                .instance(savedInstance1)
//                .user(user)
//                .build());

        likesService.addLikes(user, "kimdozzi", savedInstance1.getId());

        mockMvc.perform(get("/api/profile/likes")
                        .headers(tokenTestUtil.createAccessHeaders())
                        .contentType(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(jsonPath("$.data.content[0].instanceId").value(savedInstance1.getId()))
                .andExpect(jsonPath("$.data.content[0].title").value(savedInstance1.getTitle()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi", role = Role.ADMIN)
    @DisplayName("유저가 좋아요 목록을 조회하면, 상태 코드 200을 반환한다.")
    public void 좋아요_목록_조회_성공_2() throws Exception {

        mockMvc.perform(get("/api/profile/likes")
                        .headers(tokenTestUtil.createAccessHeaders())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.data.numberOfElements").value(0))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockCustomUser(identifier = "kimdozzi", role = Role.ADMIN)
    @DisplayName("유저가 좋아요 목록에 해당 챌린지 추가를 성공하면, 상태 코드 200을 반환한다.")
    public void 좋아요_목록_추가_성공() throws Exception {
        Long id = savedInstance1.getId();

        UserLikesAddRequest request = UserLikesAddRequest.builder()
                .identifier("kimdozzi")
                .instanceId(id)
                .build();

        mockMvc.perform(post("/api/profile/likes")
                        .headers(tokenTestUtil.createAccessHeaders())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi", role = Role.ADMIN)
    @DisplayName("유저가 좋아요 목록을 추가할 때, 사용자의 정보가 다르면 상태 코드 4xx를 반환한다.")
    public void 좋아요_목록_추가_실패_1() throws Exception {
        Long id = savedInstance1.getId();

        UserLikesAddRequest request = UserLikesAddRequest.builder()
                .identifier("park")
                .instanceId(id)
                .build();

        mockMvc.perform(post("/api/profile/likes")
                        .headers(tokenTestUtil.createAccessHeaders())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi", role = Role.ADMIN)
    @DisplayName("유저가 좋아요 목록을 추가할 때, contentType이 올바르지 않으면 상태 코드 4xx를 반환한다.")
    public void 좋아요_목록_추가_실패_2() throws Exception {
        Long id = savedInstance1.getId();

        UserLikesAddRequest request = UserLikesAddRequest.builder()
                .identifier("park")
                .instanceId(id)
                .build();

        mockMvc.perform(post("/api/profile/likes")
                        .headers(tokenTestUtil.createAccessHeaders())
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    @Test
    @WithMockCustomUser(identifier = "kimdozzi", role = Role.ADMIN)
    @DisplayName("유저가 좋아요 목록을 삭제 성공하면, 상태 코드 200을 반환한다.")
    public void 좋아요_목록_삭제_성공() throws Exception {
        User user = getSavedUser();
        Likes likes = likesRepository.save(Likes.builder()
                .instance(savedInstance1)
                .user(user)
                .build());

        Long id = likes.getId();

        mockMvc.perform(delete("/api/profile/likes/" + id)
                        .headers(tokenTestUtil.createAccessHeaders()))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(likesRepository.findById(id)).isEmpty();
    }

    @Test
    @WithMockCustomUser(identifier = "kimdozzi", role = Role.ADMIN)
    @DisplayName("유저가 좋아요 목록을 삭제 실패하면, 상태 코드 4xx을 반환한다.")
    public void 좋아요_목록_삭제_실패() throws Exception {
        User user = getSavedUser();
        Likes likes = likesRepository.save(Likes.builder()
                .instance(savedInstance1)
                .user(user)
                .build());

        mockMvc.perform(delete("/api/profile/likes/" + 2)
                        .headers(tokenTestUtil.createAccessHeaders()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname1")
                        .tags("FE, BE")
                        .providerInfo(ProviderInfo.GITHUB)
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
