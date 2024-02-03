package com.genius.gitget.admin.topic.controller;

import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.admin.topic.service.TopicService;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
public class TopicControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    TokenTestUtil tokenTestUtil;

    @Autowired
    TopicService topicService;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceService instanceService;
    @Autowired
    InstanceRepository instanceRepository;


    @BeforeEach
    public void setup() {
        //this.mockMvc = MockMvcBuilders.standaloneSetup(new TopicController()).build();
    }

    @Test
    @WithMockCustomUser
    public void 토픽_생성() throws Exception {
//        mockMvc = MockMvcBuilders.standaloneSetup(new TopicController(topicService))
//                //.webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
    }


}
