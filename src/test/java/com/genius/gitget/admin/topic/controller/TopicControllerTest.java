package com.genius.gitget.admin.topic.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.dto.TopicCreateRequest;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import com.genius.gitget.util.file.FileTestUtil;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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

        mockMvc.perform(get("/api/admin/topic?" + id).cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(1));
    }


    // @Test
    @WithMockCustomUser(role = Role.ADMIN)
    @DisplayName("토픽을 정상적으로 생성하면, 201 코드를 반환한다.")
    public void 토픽_생성() throws Exception {
        // getSavedTopic();

        // mock 파일 생성
        MockMultipartFile image = new MockMultipartFile("files", "image.png",
                "image/png", "<<png data>>".getBytes());

        // mock dto 생성
        TopicCreateRequest topicCreateRequest = TopicCreateRequest.builder()
                .title("topicTitle")
                .notice("topicNotice")
                .pointPerPerson(100)
                .description("Description")
                .tags("BE, FE")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String topicCreateRequestJson = mapper.writeValueAsString(topicCreateRequest);
        MockMultipartFile data = new MockMultipartFile("data", "data", "application/json",
                topicCreateRequestJson.getBytes(
                        StandardCharsets.UTF_8));

        // mock 이미지 타입 생성
        MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain",
                "topic".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/admin/topic")
                        .file(image)
                        .file(data)
                        .file(type)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(tokenTestUtil.createAccessCookie()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.numberOfElements").value(1));
    }

    private Topic getSavedTopic() {
        MultipartFile filename = FileTestUtil.getMultipartFile("sky");
        Files files = filesService.uploadFile(filename, "topic");

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
