package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_JOIN_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_QUIT_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.PARTICIPANT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.facade.GithubFacade;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.detail.InstanceResponse;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.dto.detail.JoinResponse;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.facade.LikesFacade;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.participant.service.ParticipantService;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class InstanceDetailServiceTest {

    @Autowired
    InstanceDetailFacade instanceDetailFacade;
    @Autowired
    LikesFacade likesFacade;
    @Autowired
    ParticipantService participantService;
    @Autowired
    GithubFacade githubFacade;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    ParticipantRepository participantRepository;

    @Value("${github.yeon-personalKey}")
    private String githubToken;
    @Value("${github.yeon-githubId}")
    private String githubId;
    @Value("${github.yeon-repository}")
    private String targetRepo;

    @Nested
    @DisplayName("챌린지 참여")
    class Describe_joinChallenge {

        @Nested
        @DisplayName("유효한 요청이 주어졌을 때")
        class Context_with_validRequest {

            private User savedUser;
            private Instance instance;
            private JoinRequest joinRequest;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                instance = getSavedInstance(Progress.PREACTIVITY);
                LocalDate todayDate = LocalDate.of(2024, 1, 30);
                joinRequest = JoinRequest.builder()
                        .instanceId(instance.getId())
                        .repository(targetRepo)
                        .todayDate(todayDate)
                        .build();
            }

            @Test
            @DisplayName("참여 정보가 저장된다.")
            void it_savesParticipantInfo() {
                JoinResponse joinResponse = instanceDetailFacade.joinNewChallenge(savedUser, joinRequest);

                assertThat(joinResponse.joinStatus()).isEqualTo(JoinStatus.YES);
                assertThat(joinResponse.joinResult()).isEqualTo(JoinResult.READY);
                assertThat(instance.getParticipantCount()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("인스턴스가 존재하지 않을 때")
        class Context_when_instanceNotExist {

            private User savedUser;
            private JoinRequest joinRequest;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                joinRequest = JoinRequest.builder()
                        .instanceId(1L) // 존재하지 않는 인스턴스 ID
                        .repository(targetRepo)
                        .build();
            }

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throwsException() {
                assertThatThrownBy(() -> instanceDetailFacade.joinNewChallenge(savedUser, joinRequest))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(INSTANCE_NOT_FOUND.getMessage());
            }
        }

        @Nested
        @DisplayName("인스턴스의 상태가 시작 전이 아닌 경우")
        class Context_when_instanceProgressNotPreactivity {

            private User savedUser;
            private Instance instance;
            private JoinRequest joinRequest;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                instance = getSavedInstance(Progress.ACTIVITY);
                joinRequest = JoinRequest.builder()
                        .repository(targetRepo)
                        .instanceId(instance.getId())
                        .todayDate(LocalDate.of(2024, 1, 30))
                        .build();
            }

            @ParameterizedTest
            @EnumSource(value = Progress.class, mode = Mode.INCLUDE, names = {"ACTIVITY", "DONE"})
            @DisplayName("예외가 발생한다.")
            void it_throwsException(Progress progress) {
                instance.updateProgress(progress);

                assertThatThrownBy(() -> instanceDetailFacade.joinNewChallenge(savedUser, joinRequest))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(CAN_NOT_JOIN_INSTANCE.getMessage());
            }
        }

        @Nested
        @DisplayName("이미 참여한 경우")
        class Context_when_userAlreadyJoined {

            private User savedUser;
            private Instance instance;
            private JoinRequest joinRequest;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                instance = getSavedInstance(Progress.PREACTIVITY);
                joinRequest = JoinRequest.builder()
                        .repository(targetRepo)
                        .instanceId(instance.getId())
                        .todayDate(LocalDate.of(2024, 1, 30))
                        .build();
                instanceDetailFacade.joinNewChallenge(savedUser, joinRequest);
            }

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throwsException() {
                assertThatThrownBy(() -> instanceDetailFacade.joinNewChallenge(savedUser, joinRequest))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(CAN_NOT_JOIN_INSTANCE.getMessage());
            }
        }

        @Nested
        @DisplayName("챌린지 시작 당일에 참여 요청을 했을 때")
        class Context_when_joinAtStartedDate {

            private User savedUser;
            private Instance instance;
            private JoinRequest joinRequest;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                instance = getSavedInstance(Progress.PREACTIVITY, LocalDate.of(2024, 1, 30));
                joinRequest = JoinRequest.builder()
                        .repository(targetRepo)
                        .instanceId(instance.getId())
                        .todayDate(LocalDate.of(2024, 1, 30))
                        .build();
            }

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throwsException() {
                assertThatThrownBy(() -> instanceDetailFacade.joinNewChallenge(savedUser, joinRequest))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(CAN_NOT_JOIN_INSTANCE.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("챌린지 취소")
    class Describe_quitChallenge {

        @Nested
        @DisplayName("아직 시작하지 않은 챌린지에 대해 취소 요청을 했을 때")
        class Context_when_quitBeforeStart {

            private User savedUser;
            private Instance savedInstance;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                savedInstance = getSavedInstance(Progress.PREACTIVITY);
            }

            @Test
            @DisplayName("ParticipantInfo가 삭제된다.")
            void it_deletesParticipantInfo() {
                instanceDetailFacade.joinNewChallenge(savedUser,
                        new JoinRequest(savedInstance.getId(), targetRepo, LocalDate.of(2024, 1, 30)));
                JoinResponse joinResponse = instanceDetailFacade.quitChallenge(savedUser, savedInstance.getId());

                assertThat(savedInstance.getParticipantCount()).isEqualTo(0);
                assertThat(joinResponse.participantId()).isNull();
                assertThat(joinResponse.joinResult()).isNull();
                assertThat(joinResponse.joinStatus()).isNull();
            }
        }

        @Nested
        @DisplayName("진행 중인 챌린지에 대해 취소 요청을 했을 때")
        class Context_when_quitDuringActivity {

            private User savedUser;
            private Instance savedInstance;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                savedInstance = getSavedInstance(Progress.PREACTIVITY);
                instanceDetailFacade.joinNewChallenge(savedUser,
                        new JoinRequest(savedInstance.getId(), targetRepo, LocalDate.of(2024, 1, 30)));
                savedInstance.updateProgress(Progress.ACTIVITY);
            }

            @Test
            @DisplayName("참여 인원 수가 줄어들고, 참여 정보가 변경된다.")
            void it_changesParticipantInfo() {
                instanceDetailFacade.quitChallenge(savedUser, savedInstance.getId());
                Participant participant = participantService.findByJoinInfo(savedUser.getId(),
                        savedInstance.getId());

                assertThat(savedInstance.getParticipantCount()).isEqualTo(0);
                assertThat(participant.getJoinResult()).isEqualTo(JoinResult.FAIL);
                assertThat(participant.getJoinStatus()).isEqualTo(JoinStatus.NO);
            }
        }

        @Nested
        @DisplayName("인스턴스가 존재하지 않을 때")
        class Context_when_instanceNotExist {

            private User savedUser;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
            }

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throwsException() {
                assertThatThrownBy(() -> instanceDetailFacade.quitChallenge(savedUser, 1L))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(INSTANCE_NOT_FOUND.getMessage());
            }
        }

        @Nested
        @DisplayName("참여 정보가 존재하지 않을 때")
        class Context_when_participantInfoNotExist {

            private User savedUser;
            private Instance savedInstance;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                savedInstance = getSavedInstance(Progress.PREACTIVITY);
            }

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throwsException() {
                assertThatThrownBy(() -> instanceDetailFacade.quitChallenge(savedUser, savedInstance.getId()))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(PARTICIPANT_NOT_FOUND.getMessage());
            }
        }

        @Nested
        @DisplayName("진행 상황이 DONE인 챌린지에 대해 취소 요청을 했을 때")
        class Context_when_progressIsDone {

            @Test
            @DisplayName("인스턴스의 진행 상황이 DONE이면 예외가 발생한다.")
            public void should_throwException_when_progressIsDONE() {
                //given
                User savedUser = getSavedUser(githubId);
                Instance savedInstance = getSavedInstance(Progress.PREACTIVITY);
                LocalDate todayDate = LocalDate.of(2024, 1, 30);
                JoinRequest joinRequest = JoinRequest.builder()
                        .instanceId(savedInstance.getId())
                        .repository(targetRepo)
                        .todayDate(todayDate)
                        .build();

                //when
                instanceDetailFacade.joinNewChallenge(savedUser, joinRequest);
                savedInstance.updateProgress(Progress.DONE);

                //then
                assertThatThrownBy(() -> instanceDetailFacade.quitChallenge(savedUser, savedInstance.getId()))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(CAN_NOT_QUIT_INSTANCE.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("상세 조회")
    class Describe_getInstanceDetailInformation {

        @Nested
        @DisplayName("사용자가 참여한 인스턴스의 상세 정보를 조회할 때")
        class Context_when_joinedInstance {

            private User savedUser;
            private Instance savedInstance;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                savedInstance = getSavedInstance(Progress.PREACTIVITY, LocalDate.now().plusDays(2));
            }

            @Test
            @DisplayName("필요한 데이터를 반환한다.")
            void it_returnsInstanceDetail() {
                instanceDetailFacade.joinNewChallenge(savedUser,
                        new JoinRequest(savedInstance.getId(), targetRepo, LocalDate.of(2024, 1, 30)));
                InstanceResponse instanceResponse = instanceDetailFacade.getInstanceDetailInformation(savedUser,
                        savedInstance.getId());

                assertThat(instanceResponse.instanceId()).isEqualTo(savedInstance.getId());
                assertThat(instanceResponse.progress()).isEqualTo(Progress.PREACTIVITY);
                assertThat(instanceResponse.remainDays()).isEqualTo(2);
                assertThat(instanceResponse.participantCount()).isEqualTo(1);
                assertThat(instanceResponse.pointPerPerson()).isEqualTo(100);
                assertThat(instanceResponse.description()).isEqualTo(savedInstance.getDescription());
                assertThat(instanceResponse.joinStatus()).isEqualTo(JoinStatus.YES);
                assertThat(instanceResponse.likesInfo().likesCount()).isEqualTo(0);
                assertThat(instanceResponse.likesInfo().likesId()).isEqualTo(0);
                assertThat(instanceResponse.likesInfo().isLiked()).isFalse();
            }
        }

        @Nested
        @DisplayName("사용자가 참여하지 않은 인스턴스의 상세 정보를 조회할 때")
        class Context_when_notJoinedInstance {

            private User savedUser;
            private Instance savedInstance;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                savedInstance = getSavedInstance(Progress.PREACTIVITY, LocalDate.now().plusDays(2));
            }

            @Test
            @DisplayName("필요한 정보를 반환할 수 있다.")
            void it_returnsInstanceDetail() {
                InstanceResponse instanceResponse = instanceDetailFacade.getInstanceDetailInformation(savedUser,
                        savedInstance.getId());

                assertThat(instanceResponse.instanceId()).isEqualTo(savedInstance.getId());
                assertThat(instanceResponse.remainDays()).isEqualTo(2);
                assertThat(instanceResponse.participantCount()).isEqualTo(0);
                assertThat(instanceResponse.pointPerPerson()).isEqualTo(100);
                assertThat(instanceResponse.description()).isEqualTo(savedInstance.getDescription());
                assertThat(instanceResponse.joinStatus()).isEqualTo(JoinStatus.NO);
                assertThat(instanceResponse.likesInfo().likesCount()).isEqualTo(0);
                assertThat(instanceResponse.likesInfo().likesId()).isEqualTo(0);
                assertThat(instanceResponse.likesInfo().isLiked()).isFalse();
            }
        }

        @Nested
        @DisplayName("좋아요를 한 이후 상세 정보를 조회할 때")
        class Context_when_userPushLikes {

            private User savedUser;
            private Instance savedInstance;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
                savedInstance = getSavedInstance(Progress.PREACTIVITY, LocalDate.now().plusDays(2));
                likesFacade.addLikes(savedUser, savedUser.getIdentifier(), savedInstance.getId());
            }

            @Test
            @DisplayName("좋아요 관련된 정보를 반환한다.")
            void it_returnsLikesData() {
                InstanceResponse instanceResponse = instanceDetailFacade.getInstanceDetailInformation(savedUser,
                        savedInstance.getId());

                assertThat(instanceResponse.instanceId()).isEqualTo(savedInstance.getId());
                assertThat(instanceResponse.remainDays()).isEqualTo(2);
                assertThat(instanceResponse.participantCount()).isEqualTo(0);
                assertThat(instanceResponse.pointPerPerson()).isEqualTo(100);
                assertThat(instanceResponse.description()).isEqualTo(savedInstance.getDescription());
                assertThat(instanceResponse.joinStatus()).isEqualTo(JoinStatus.NO);
                assertThat(instanceResponse.likesInfo().likesCount()).isEqualTo(1);
                assertThat(instanceResponse.likesInfo().likesId()).isNotEqualTo(0);
                assertThat(instanceResponse.likesInfo().isLiked()).isTrue();
            }
        }

        @Nested
        @DisplayName("상세 정보를 요청하는 인스턴스가 존재하지 않을 때")
        class Context_when_instanceNotExist {

            private User savedUser;

            @BeforeEach
            void setUp() {
                savedUser = getSavedUser(githubId);
            }

            @Test
            @DisplayName("예외가 발생해야 한다.")
            void it_throwsException() {
                assertThatThrownBy(() -> instanceDetailFacade.getInstanceDetailInformation(savedUser, 1L))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(INSTANCE_NOT_FOUND.getMessage());
            }
        }
    }

    // 유틸리티 메서드
    private User getSavedUser(String githubId) {
        User user = userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier(githubId)
                        .information("information")
                        .tags("BE,FE")
                        .build()
        );
        githubFacade.registerGithubPersonalToken(user, githubToken);
        return user;
    }

    private Instance getSavedInstance(Progress progress) {
        return instanceRepository.save(
                Instance.builder()
                        .progress(progress)
                        .startedDate(LocalDateTime.of(2024, 2, 1, 11, 3))
                        .build()
        );
    }

    private Instance getSavedInstance(Progress progress, LocalDate startedDate) {
        return instanceRepository.save(
                Instance.builder()
                        .progress(progress)
                        .description("description")
                        .notice("notice")
                        .certificationMethod("certification method")
                        .pointPerPerson(100)
                        .startedDate(startedDate.atTime(12, 12))
                        .completedDate(startedDate.plusDays(30).atTime(12, 12))
                        .build()
        );
    }
}
