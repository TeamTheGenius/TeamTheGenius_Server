package com.genius.gitget.admin.topic.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import com.genius.gitget.util.file.FileTestUtil;
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
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
public class TopicControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    TokenTestUtil tokenTestUtil;

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    FilesService filesService;
    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("토픽 상세 정보를 요청하면, 해당 토픽의 정보를 반환한다.")
    public void 토픽_상세정보_요청() throws Exception {
        Topic savedTopic = getSavedTopic();
        Long id = savedTopic.getId();

        mockMvc.perform(get("/api/admin/topic/" + id).cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("title"));
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("토픽 리스트를 요청하면, 해당 토픽의 정보를 리스트로 반환한다.")
    public void 토픽_리스트_요청() throws Exception {
        getSavedTopic();
        getSavedTopic();
        getSavedTopic();

        mockMvc.perform(get("/api/admin/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(3))
                .andExpect(jsonPath("$.data.content[0].title").value("title"))
                .andExpect(jsonPath("$.data.content[1].title").value("title"))
                .andExpect(jsonPath("$.data.content[2].title").value("title"))
                .andExpect(jsonPath("$.data.content[3].title").doesNotExist());
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("토픽 삭제 성공하면, 200 상태코드를 반환한다.")
    public void 토픽_삭제_성공() throws Exception {
        Topic savedTopic = getSavedTopic();
        Long id = savedTopic.getId();

        mockMvc.perform(delete("/api/admin/topic/" + id).cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").doesNotExist());

        Assertions.assertThat(topicRepository.findById(id)).isEmpty();
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("토픽 삭제 실패하면, 4xx 상태코드를 반환한다.")
    public void 토픽_삭제_실패() throws Exception {
        Topic savedTopic = getSavedTopic();
        Long id = savedTopic.getId();

        mockMvc.perform(delete("/api/admin/topic/" + id + 1).cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    private Topic getSavedTopic() {
        MultipartFile filename = FileTestUtil.getMultipartFile("sky");
        Files files = filesService.uploadFile(filename, FileType.TOPIC);

        Topic topic = topicRepository.save(
                Topic.builder()
                        .title("title")
                        .notice("notice")
                        .description("description")
                        .tags("BE")
                        .pointPerPerson(100)
                        .build()
        );
        topic.setFiles(files);

        return topic;
    }
}
