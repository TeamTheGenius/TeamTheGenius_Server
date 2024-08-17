package com.genius.gitget.profile.service;

import static com.genius.gitget.global.security.constants.ProviderInfo.GITHUB;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import com.genius.gitget.signout.Signout;
import com.genius.gitget.signout.SignoutRepository;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
public class ProfileServiceTest {

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
    ProfileFacade profileFacade;
    @Autowired
    SignoutRepository signoutRepository;

    static User user1, user2;
    static Topic topic1;
    static Instance instance1, instance2, instance3;

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

    @Nested
    @DisplayName("유저 상세 정보 조회")
    class Describe_getUserDetailsInformation {

        @Test
        @DisplayName("유저의 상세 정보를 반환한다.")
        void it_returns_user_details_information() {
            UserDetailsInformationResponse userDetailsInformation = profileFacade.getUserDetailsInformation(user1);
            Assertions.assertThat(userDetailsInformation.getIdentifier()).isEqualTo("neo5188@gmail.com");
        }
    }

    @Nested
    @DisplayName("유저 정보 조회")
    class Describe_getUserInformation {

        @Test
        @DisplayName("유저의 정보를 반환한다.")
        void it_returns_user_information() {
            List<Long> userIdList = new ArrayList<>();
            List<User> all = userRepository.findAll();
            for (User user : all) {
                Long id = user.getId();
                userIdList.add(id);
            }

            for (int i = userIdList.size() - 1; i >= 0; i--) {
                UserInformationResponse userInformation = profileFacade.getUserInformation(userIdList.get(i));
                Assertions.assertThat(userInformation.getNickname()).isEqualTo("alias" + (i + 1));
            }
        }
    }

    @Nested
    @DisplayName("유저 정보 수정")
    class Describe_updateUserInformation {

        @Test
        @DisplayName("유저의 정보를 수정한다.")
        void it_updates_user_information() {
            profileFacade.updateUserInformation(user1,
                    UserInformationUpdateRequest.builder()
                            .nickname("수정된 nickname")
                            .information("수정된 information")
                            .build());

            User user = userRepository.findByIdentifier(user1.getIdentifier())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

            Assertions.assertThat(user.getNickname()).isEqualTo("수정된 nickname");
        }
    }

    @Nested
    @DisplayName("유저 관심사 조회")
    class Describe_getUserInterest {

        @Test
        @DisplayName("유저의 관심사를 반환한다.")
        void it_returns_user_interest() {
            UserInterestResponse userInterest = profileFacade.getUserInterest(user1);
            List<String> tags = userInterest.getTags();
            String join = String.join(",", tags);
            Assertions.assertThat(join).isEqualTo("BE,FE");
        }
    }

    @Nested
    @DisplayName("유저 관심사 수정")
    class Describe_updateUserTags {

        @Test
        @DisplayName("유저의 관심사를 수정한다.")
        void it_updates_user_tags() {
            profileFacade.updateUserTags(user1,
                    UserInterestUpdateRequest.builder().tags(new ArrayList<>(Arrays.asList("FE", "BE"))).build());
            User user = userRepository.findByIdentifier(user1.getIdentifier())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
            Assertions.assertThat(user.getTags()).isEqualTo("FE,BE");
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class Describe_deleteUserInformation {

        @Test
        @DisplayName("유저의 정보를 삭제한다.")
        void it_deletes_user_information() {
            User user = userRepository.findByIdentifier(user1.getIdentifier())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
            String userIdentifier = user.getIdentifier();
            profileFacade.deleteUserInformation(user, "서비스 이용 불편");

            assertThrows(BusinessException.class,
                    () -> userRepository.findByIdentifier(userIdentifier)
                            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND)));

            Signout byIdentifier = signoutRepository.findByIdentifier(userIdentifier);

            Assertions.assertThat(byIdentifier.getReason()).isEqualTo("서비스 이용 불편");
        }
    }

    @Nested
    @DisplayName("유저 포인트 조회")
    class Describe_getUserPoint {

        @Test
        @DisplayName("유저의 포인트를 반환한다.")
        void it_returns_user_point() {
            User user = userRepository.findByIdentifier(user1.getIdentifier())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
            user.updatePoints(1500L);
            userRepository.save(user);
            UserPointResponse userPoint = profileFacade.getUserPoint(user1);
            Assertions.assertThat(userPoint.getPoint()).isEqualTo(1500);
        }
    }

    @Nested
    @DisplayName("챌린지 현황 조회")
    class Describe_getUserChallengeResult {

        @Test
        @DisplayName("유저의 챌린지 현황을 반환한다.")
        void it_returns_user_challenge_result() {
            // TODO 챌린지 현황 조회
            User user = userRepository.findByIdentifier(user1.getIdentifier())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

            UserChallengeResultResponse userChallengeResult = profileFacade.getUserChallengeResult(user);
            System.out.println(userChallengeResult.getBeforeStart());
            System.out.println(userChallengeResult.getProcessing());
            System.out.println(userChallengeResult.getFail());
            System.out.println(userChallengeResult.getSuccess());
        }
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
