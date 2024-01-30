package com.genius.gitget.topic;

import com.genius.gitget.admin.topic.service.TopicService;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class TopicControllerTest {
    MockMvc mockMvc;
    @Autowired
    private TokenTestUtil tokenTestUtil;
    @Autowired
    WebApplicationContext context;

    @MockBean
    TopicService topicService;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockCustomUser
    public void 토픽_생성() throws Exception {
        //given

        //when
        mockMvc.perform(get("/api/admin/topic")
                        .cookie(tokenTestUtil.createAccessCookie()))
                .andExpect(status().isOk());
    }

}
