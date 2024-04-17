package com.genius.gitget.profile.service;

import static com.genius.gitget.global.security.constants.ProviderInfo.GITHUB;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.genius.gitget.admin.signout.Signout;
import com.genius.gitget.admin.signout.SignoutRepository;
import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.likes.service.LikesService;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.profile.dto.UserChallengeResultResponse;
import com.genius.gitget.profile.dto.UserDetailsInformationResponse;
import com.genius.gitget.profile.dto.UserInformationResponse;
import com.genius.gitget.profile.dto.UserInformationUpdateRequest;
import com.genius.gitget.profile.dto.UserInterestResponse;
import com.genius.gitget.profile.dto.UserInterestUpdateRequest;
import com.genius.gitget.profile.dto.UserPointResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
public class ProfileServiceTest {
    static User user1, user2;
    static Topic topic1;
    static Instance instance1, instance2, instance3;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    LikesRepository likesRepository;
    @Autowired
    LikesService likesService;
    @Autowired
    ProfileService profileService;
    @Autowired
    SignoutRepository signoutRepository;

    @BeforeEach
    void setup() {
        user1 = getSavedUser("neo5188@gmail.com", GITHUB, "alias1");
        user2 = getSavedUser("neo7269@naver.com", GITHUB, "alias2");

        topic1 = getSavedTopic("1일 1커밋", "BE");

        instance1 = getSavedInstance("1일 1커밋", "BE", 50);
        instance2 = getSavedInstance("1일 1커밋", "BE", 100);
        instance3 = getSavedInstance("1일 1알고리즘", "CS,BE,FE", 500);

        //== 연관관계 ==//
        instance1.setTopic(topic1);
        instance2.setTopic(topic1);
        instance3.setTopic(topic1);

        Likes likes1 = new Likes(user1, instance1);
        Likes likes2 = new Likes(user1, instance2);
        Likes likes3 = new Likes(user1, instance3);
        likesRepository.save(likes1);
        likesRepository.save(likes2);
        likesRepository.save(likes3);
    }

    // TODO 챌린지 현황 조회 -> 코드 병합 후 테스트할 것

    @Test
    void 유저_상세_조회() {
        UserDetailsInformationResponse userDetailsInformation = profileService.getUserDetailsInformation(user1);
        Assertions.assertThat(userDetailsInformation.getIdentifier()).isEqualTo("neo5188@gmail.com");
    }

    @Test
    void 유저_조회() {
        List<Long> userIdList = new ArrayList<>();
        List<User> all = userRepository.findAll();
        for (User user : all) {
            Long id = user.getId();
            userIdList.add(id);
        }

        for (int i = userIdList.size() - 1; i >= 0; i--) {
            UserInformationResponse userInformation = profileService.getUserInformation(userIdList.get(i));
            Assertions.assertThat(userInformation.getNickname()).isEqualTo("alias" + (i + 1));
        }
    }

    @Test
    void 유저_정보_수정() {
        profileService.updateUserInformation(user1,
                UserInformationUpdateRequest.builder()
                        .nickname("수정된 nickname")
                        .information("수정된 information")
                        .build());

        User user = userRepository.findByIdentifier(user1.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Assertions.assertThat(user.getNickname()).isEqualTo("수정된 nickname");
    }

    @Test
    void 유저_관심사_조회() {
        UserInterestResponse userInterest = profileService.getUserInterest(user1);
        List<String> tags = userInterest.getTags();
        String join = String.join(",", tags);
        Assertions.assertThat(join).isEqualTo("BE,FE");
    }

    @Test
    void 유저_관심사_수정() {
        profileService.updateUserTags(user1,
                UserInterestUpdateRequest.builder().tags(new ArrayList<>(Arrays.asList("FE", "BE"))).build());
        User user = userRepository.findByIdentifier(user1.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Assertions.assertThat(user.getTags()).isEqualTo("FE,BE");
    }


    @Test
    void 회원_탈퇴() {
        User user = userRepository.findByIdentifier(user1.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        String userIdentifier = user.getIdentifier();
        profileService.deleteUserInformation(user, "서비스 이용 불편");

        assertThrows(BusinessException.class,
                () -> userRepository.findByIdentifier(userIdentifier)
                        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND)));

        Signout byIdentifier = signoutRepository.findByIdentifier(userIdentifier);

        Assertions.assertThat(byIdentifier.getReason()).isEqualTo("서비스 이용 불편");
    }

    @Test
    void 유저_포인트_조회() {
        User user = userRepository.findByIdentifier(user1.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        user.updatePoints(1500L);
        userRepository.save(user);
        UserPointResponse userPoint = profileService.getUserPoint(user1);
        Assertions.assertThat(userPoint.getPoint()).isEqualTo(1500);
    }

    @Test
    void 챌린지_현황_조회() {
        // TODO 챌린지 현황 조회
        User user = userRepository.findByIdentifier(user1.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        UserChallengeResultResponse userChallengeResult = profileService.getUserChallengeResult(user);
        System.out.println(userChallengeResult.getBeforeStart());
        System.out.println(userChallengeResult.getProcessing());
        System.out.println(userChallengeResult.getFail());
        System.out.println(userChallengeResult.getSuccess());
    }


    private User getSavedUser(String identifier, ProviderInfo providerInfo, String nickname) {
        User user = userRepository.save(
                User.builder()
                        .identifier(identifier)
                        .providerInfo(providerInfo)
                        .role(Role.ADMIN)
                        .tags("BE,FE")
                        .nickname(nickname)
                        .build()
        );
        return user;
    }

    private Topic getSavedTopic(String title, String tags) {
        Topic topic = topicRepository.save(
                Topic.builder()
                        .title(title)
                        .tags(tags)
                        .description("토픽 설명")
                        .pointPerPerson(100)
                        .build()
        );
        return topic;
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
                        .certificationMethod("인증 방법")
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        return instance;
    }
}
