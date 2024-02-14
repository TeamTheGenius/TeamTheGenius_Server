package com.genius.gitget.challenge.home.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import java.time.LocalDateTime;
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
class InstanceHomeControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    TokenTestUtil tokenTestUtil;

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceRepository instanceRepository;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    @DisplayName("특정 사용자의 관심사 태그를 확인하여, 태그와 일치하는 인스턴스들을 참여 인원 순으로 반환한다.")
    @WithMockCustomUser
    public void should_returnInstances_when_passUserTags() throws Exception {
        //given
        getSavedInstance("title1", "BE", 20);
        getSavedInstance("title2", "BE", 34);
        getSavedInstance("title3", "FE", 10);
        getSavedInstance("title4", "AI", 2);

        //when & then
        mockMvc.perform(get("/api/challenges/recommend")
                        .cookie(tokenTestUtil.createAccessCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(3));
    }


    private Instance getSavedInstance(String title, String tags, int participantCnt) {
        LocalDateTime now = LocalDateTime.now();
        Instance instance = instanceRepository.save(
                Instance.builder()
                        .tags(tags)
                        .title(title)
                        .description("description")
                        .progress(Progress.PREACTIVITY)
                        .pointPerPerson(100)
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        instance.setTopic(getSavedTopic());
        return instance;
    }

    private Topic getSavedTopic() {
        return topicRepository.save(
                Topic.builder()
                        .title("title")
                        .description("description")
                        .tags("BE")
                        .pointPerPerson(100)
                        .build()
        );
    }
}