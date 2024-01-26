//package com.genius.gitget.util;
//
//
//import com.genius.gitget.instance.domain.Instance;
//import com.genius.gitget.instance.domain.Progress;
//import com.genius.gitget.participantinfo.domain.JoinResult;
//import com.genius.gitget.participantinfo.domain.JoinStatus;
//import com.genius.gitget.participantinfo.domain.ParticipantInfo;
//import com.genius.gitget.security.constants.ProviderInfo;
//import com.genius.gitget.topic.domain.Topic;
//import com.genius.gitget.user.domain.User;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//
//import static com.genius.gitget.security.constants.ProviderInfo.GOOGLE;
//import static com.genius.gitget.user.domain.Role.ADMIN;
//import static com.genius.gitget.user.domain.Role.USER;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//
//public class TestBaseUtil {
//    public User createTestUser() {
//        User userA = User.builder().identifier("neo5188@gmail.com")
//                .providerInfo(ProviderInfo.NAVER)
//                .nickname("kimdozzi")
//                .information("백엔드")
//                .interest("운동")
//                .role(ADMIN)
//                .build();
//
//        User userB = User.builder().identifier("ssang23@naver.com")
//                .providerInfo(GOOGLE)
//                .nickname("SEONG")
//                .information("프론트엔드")
//                .interest("영화")
//                .role(USER)
//                .build();
//
//
//    };
//
//    public Topic createTestTopic() {
//
//    }
//}
//    public void setup() {
//
//
//        instance1 = Instance.builder()
//                .title("1일 1커밋")
//                .description("챌린지 세부사항입니다.")
//                .pointPerPerson(10)
//                .tags("BE, CS")
//                .progress(Progress.ACTIVITY)
//                .startedDate(LocalDateTime.now())
//                .completedDate(LocalDateTime.now().plusDays(3))
//                .build();
//
//        topic1 = Topic.builder()
//                .title("1일 1커밋")
//                .description("간단한 설명란")
//                .pointPerPerson(300)
//                .tags("BE, CS")
//                .build();
//
//        topic2 = Topic.builder()
//                .title("1일 2커밋")
//                .description("간단한 설명란")
//                .pointPerPerson(300)
//                .tags("BE, CS")
//                .build();
//
//
//        participantInfo1 = ParticipantInfo.builder()
//                .joinResult(JoinResult.PROCESSING)
//                .joinStatus(JoinStatus.YES)
//                .build();
//
//        participantInfo2 = ParticipantInfo.builder()
//                .joinResult(JoinResult.SUCCESS)
//                .joinStatus(JoinStatus.YES)
//                .build();
//
//
//        userRepository.save(user1);
//        userRepository.save(user2);
//
//        topicRepository.save(topic1);
//        topicRepository.save(topic2);
//
//        topic1.setInstance(instance1);
//        instance1.setTopic(topic1);
//        instanceRepository.save(instance1);
//
//        participantInfo1.setUserAndInstance(user1, instance1);
//        participantInfoRepository.save(participantInfo1);
//        participantInfo2.setUserAndInstance(user2, instance1);
//        participantInfoRepository.save(participantInfo2);
//
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//    }