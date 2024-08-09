package com.genius.gitget.challenge.instance.controller;


import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.global.file.service.FilesManager;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
public class InstanceControllerTest {
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
    FilesManager filesManager;

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

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("인스턴스 리스트 조회를 요청하면, 상태코드 200반환과 함께 인스턴스 리스트를 반환한다.")
    public void 인스턴스_리스트_조회() throws Exception {
        mockMvc.perform(get("/api/admin/instance").headers(tokenTestUtil.createAccessHeaders()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2));
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("특정 토픽에 대한 리스트 조회를 요청하면, 상태코드 200과 함께 데아터를 반환한다.")
    public void 특정_토픽에_대한_리스트_조회_1() throws Exception {
        Long id = savedTopic1.getId();

        mockMvc.perform(get("/api/admin/topic/instances/" + id).headers(tokenTestUtil.createAccessHeaders()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2))
                .andExpect(jsonPath("$.data.content[0].title").value("title1"))
                .andExpect(jsonPath("$.data.content[1].title").value("title2"));
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("특정 토픽에 대한 리스트 조회를 요청하면, 상태코드 200과 함께 데아터를 반환한다.")
    public void 특정_토픽에_대한_리스트_조회_2() throws Exception {
        Long id = savedTopic2.getId();

        mockMvc.perform(get("/api/admin/topic/instances/" + id).headers(tokenTestUtil.createAccessHeaders()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.numberOfElements").value(0));
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("인스턴스 단건 조회를 하면, 상태코드 200과 함께 인스턴스 상세정보를 반환한다.")
    public void 인스턴스_단건_조회() throws Exception {
        Long topicId = savedTopic1.getId();

        Long instanceId = savedInstance2.getId();

        mockMvc.perform(get("/api/admin/instance/" + instanceId).headers(tokenTestUtil.createAccessHeaders()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.topicId").value(topicId))
                .andExpect(jsonPath("data.instanceId").value(instanceId));
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("인스턴스 삭제 성공하면, 상태코드 200을 반환한다.")
    public void 인스턴스_삭제_성공() throws Exception {
        Long instanceId = savedInstance1.getId();

        mockMvc.perform(delete("/api/admin/instance/" + instanceId).headers(tokenTestUtil.createAccessHeaders()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").doesNotExist());

        Assertions.assertThat(instanceRepository.findById(instanceId)).isEmpty();
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("인스턴스 삭제 실패하면, 상태코드 4xx을 반환한다.")
    public void 인스턴스_삭제_성공_실패() throws Exception {
        Long instanceId = savedInstance1.getId();

        mockMvc.perform(delete("/api/admin/instance/" + instanceId + 1).headers(tokenTestUtil.createAccessHeaders()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
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
