package com.genius.gitget.topic;

import static com.genius.gitget.challenge.user.domain.Role.ADMIN;
import static com.genius.gitget.challenge.user.domain.Role.USER;
import static com.genius.gitget.global.security.constants.ProviderInfo.GOOGLE;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.admin.topic.service.TopicService;
import com.genius.gitget.challenge.hits.repository.HitsRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantInfoRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.util.TokenTestUtil;
import com.genius.gitget.util.WithMockCustomUser;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static com.genius.gitget.challenge.user.domain.Role.USER;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ TopicControllerTest.class })
@WithMockCustomUser(role = Role.USER)
@MockBean(JpaMetamodelMappingContext.class)
public class TopicControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private TokenTestUtil tokenTestUtil;
    @Autowired
    WebApplicationContext context;

    @MockBean
    TopicService topicService;


    @BeforeEach
    public void setup() {
        user1 = User.builder().identifier("neo5188@gmail.com")
                .providerInfo(ProviderInfo.NAVER)
                .nickname("kimdozzi")
                .information("백엔드")
                .tags("운동")
                .role(ADMIN)
                .build();

        user2 = User.builder().identifier("ssang23@naver.com")
                .providerInfo(GOOGLE)
                .nickname("SEONG")
                .information("프론트엔드")
                .tags("영화")
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

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

//    @Test
//    public void 토픽_생성() throws Exception {
//        //given
//
//        //when
//        mockMvc.perform(get("/api/admin/topic")
//                        .cookie(tokenTestUtil.createAccessCookie()))
//                .andExpect(status().isOk());
//    }

}
