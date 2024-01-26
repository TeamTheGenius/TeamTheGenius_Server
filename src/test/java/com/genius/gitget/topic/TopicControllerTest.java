package com.genius.gitget.topic;

import static com.genius.gitget.global.security.constants.ProviderInfo.GOOGLE;
import static com.genius.gitget.challenge.user.domain.Role.ADMIN;
import static com.genius.gitget.challenge.user.domain.Role.USER;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.genius.gitget.challenge.hits.repository.HitsRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantInfoRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.admin.topic.service.TopicService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class TopicControllerTest {

    protected MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    HitsRepository hitsRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    ParticipantInfoRepository participantInfoRepository;
    @Autowired
    private TokenTestUtil tokenTestUtil;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicService topicService;
    private User user1, user2;
    private Instance instance1;
    private Topic topic1, topic2;
    private ParticipantInfo participantInfo1;
    private ParticipantInfo participantInfo2;

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
                .pointPerPerson(10)
                .tags("BE, CS")
                .progress(Progress.ACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(3))
                .build();

        topic1 = Topic.builder()
                .title("1일 1커밋")
                .description("간단한 설명란")
                .pointPerPerson(300)
                .tags("BE, CS")
                .build();

        topic2 = Topic.builder()
                .title("1일 2커밋")
                .description("간단한 설명란")
                .pointPerPerson(300)
                .tags("BE, CS")
                .build();

        participantInfo1 = ParticipantInfo.builder()
                .joinResult(JoinResult.PROCESSING)
                .joinStatus(JoinStatus.YES)
                .build();

        participantInfo2 = ParticipantInfo.builder()
                .joinResult(JoinResult.SUCCESS)
                .joinStatus(JoinStatus.YES)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        topicRepository.save(topic1);
        topicRepository.save(topic2);

        topic1.setInstance(instance1);
        instance1.setTopic(topic1);
        instanceRepository.save(instance1);

        participantInfo1.setUserAndInstance(user1, instance1);
        participantInfoRepository.save(participantInfo1);
        participantInfo2.setUserAndInstance(user2, instance1);
        participantInfoRepository.save(participantInfo2);

    }

    @Test
    @WithMockCustomUser
    public void 토픽_조회() throws Exception {
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        List<Topic> topics = Arrays.asList(topic1, topic2);
        PageImpl<Topic> topicPage = new PageImpl<>(topics, pageable, topics.size());

//        when(topicService.getAllTopics(pageable)).thenReturn(topicPage);

        for (Topic topic : topicPage) {
            System.out.println("topic.getInstanceList() = " + topic.getInstanceList());
            System.out.println("topic.getTitle() = " + topic.getTitle());
        }

        System.out.println("topics.size() = " + topics.size());

        // When & Then
        mockMvc.perform(get("/api/admin/topic?page=0&size=5")
                        .cookie(tokenTestUtil.createAccessCookie())
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title").value("1일 1커밋"))
                .andExpect(jsonPath("$.content[0].tags").value("BE, CS"))
                .andExpect(jsonPath("$.content[0].description").value("간단한 설명란"))
                .andExpect(jsonPath("$.content[0].point_per_person").value(300))
                .andExpect(jsonPath("$.content[1].title").value("1일 2커밋"))
                .andExpect(jsonPath("$.content[1].tags").value("BE, CS"))
                .andExpect(jsonPath("$.content[1].description").value("간단한 설명란"))
                .andExpect(jsonPath("$.content[1].point_per_person").value(300));
    }
}
