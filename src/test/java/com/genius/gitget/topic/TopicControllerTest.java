package com.genius.gitget.topic;

import com.genius.gitget.hits.repository.HitsRepository;
import com.genius.gitget.instance.domain.Instance;
import com.genius.gitget.instance.domain.Progress;
import com.genius.gitget.instance.repository.InstanceRepository;
import com.genius.gitget.security.constants.ProviderInfo;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import com.genius.gitget.topic.service.TopicService;
import com.genius.gitget.user.domain.User;
import com.genius.gitget.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.genius.gitget.security.constants.ProviderInfo.GOOGLE;
import static com.genius.gitget.user.domain.Role.ADMIN;
import static com.genius.gitget.user.domain.Role.USER;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TopicControllerTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    HitsRepository hitsRepository;
    @Autowired
    TopicRepository topicRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicService topicService;

    protected MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    private User user1, user2;
    private Instance instance1;
    private Topic topic1;

    @BeforeEach
    public void setup() {
        user1 = User.builder().identifier("neo5188@gmail.com")
                .providerInfo(ProviderInfo.NAVER)
                .nickname("kimdozzi")
                .information("백엔드")
                .interest("운동")
                .role(ADMIN)
                .build();

        user2 = User.builder().identifier("ssang23@naver.com")
                .providerInfo(GOOGLE)
                .nickname("SEONG")
                .information("프론트엔드")
                .interest("영화")
                .role(USER)
                .build();

        instance1 = Instance.builder()
                .title("1일 1커밋")
                .description("챌린지 세부사항입니다.")
                .point_per_person(10)
                .tags("BE, CS")
                .progress(Progress.ACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();

        topic1 = Topic.builder()
                .title("1일 1커밋")
                .description("간단한 설명란")
                .point_per_person(300)
                .tags("BE, CS")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        topicRepository.save(topic1);
        topic1.setInstance(instance1);
        instance1.setTopic(topic1);
        instanceRepository.save(instance1);
    }

    @Test
    public void 토픽_조회() throws Exception {


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
