package com.genius.gitget.topic;

import com.genius.gitget.topic.controller.TopicController;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.service.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.awaitility.Awaitility.given;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicService topicService;

    protected MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Test
    public void 토픽_조회() throws Exception {
        List<Topic> topics = Arrays.asList(
               new Topic("1일 1커밋", "챌린지입니다.", "BE, CS", 500),
                new Topic("블로그 작성", "챌린지 테스트입니다.", "BE", 1500)
        );
        when(topicService.getAllTopics()).thenReturn(topics);

        // When & Then
        mockMvc.perform(get("/api/admin/topic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("1일 1커밋"))
                .andExpect(jsonPath("$[0].tags").value("BE, CS"))
                .andExpect(jsonPath("$[0].description").value("챌린지입니다."))
                .andExpect(jsonPath("$[0].point_per_person").value(500))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("블로그 작성"))
                .andExpect(jsonPath("$[1].tags").value("BE"))
                .andExpect(jsonPath("$[1].description").value("챌린지 테스트입니다."))
                .andExpect(jsonPath("$[1].point_per_person").value(1500));

    }
}
