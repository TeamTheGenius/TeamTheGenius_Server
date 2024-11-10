package com.genius.gitget.challenge.myChallenge.service;

import static com.genius.gitget.challenge.participant.domain.JoinResult.FAIL;
import static com.genius.gitget.challenge.participant.domain.JoinResult.SUCCESS;
import static com.genius.gitget.challenge.participant.domain.RewardStatus.NO;
import static com.genius.gitget.challenge.participant.domain.RewardStatus.YES;
import static com.genius.gitget.store.item.domain.ItemCategory.CERTIFICATION_PASSER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.myChallenge.facade.MyChallengeFacade;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.util.certification.CertificationFactory;
import com.genius.gitget.util.instance.InstanceFactory;
import com.genius.gitget.util.participant.ParticipantFactory;
import com.genius.gitget.util.store.StoreFactory;
import com.genius.gitget.util.user.UserFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class MyChallengeFacadeTest {
    private LocalDate localDate = LocalDate.now();
    private User user;
    private Instance instance1;
    private Instance instance2;
    private Instance instance3;

    @Autowired
    private MyChallengeFacade myChallengeFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private CertificationRepository certificationRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrdersRepository ordersRepository;

    @BeforeEach
    void setup() {
        user = userRepository.save(UserFactory.createUser());
    }


    @Nested
    @DisplayName("시작 전 인스턴스들을 조회할 때")
    class context_inquiry_preActivity_instances {
        @BeforeEach
        void setup() {
            instance1 = instanceRepository.save(InstanceFactory.createPreActivity(10));
            instance2 = instanceRepository.save(InstanceFactory.createPreActivity(10));
            instance3 = instanceRepository.save(InstanceFactory.createPreActivity(10));
        }

        @Nested
        @DisplayName("참여한 인스턴스 중 시작 전인 인스턴스들이 있을 때")
        class describe_joined_preActivity_instance {
            @BeforeEach
            void setup() {
                participantRepository.save(ParticipantFactory.createPreActivity(user, instance1));
                participantRepository.save(ParticipantFactory.createPreActivity(user, instance2));
                participantRepository.save(ParticipantFactory.createPreActivity(user, instance3));
            }

            @Test
            @DisplayName("인스턴스 목록들을 조회할 수 있다.")
            public void it_returns_instance_list() {
                List<PreActivityResponse> preActivityInstances = myChallengeFacade.getPreActivityInstances(user,
                        localDate);
                assertThat(preActivityInstances.size()).isEqualTo(3);
            }
        }
    }

    @Nested
    @DisplayName("진행 중인 인스턴스 조회 시")
    class context_inquiry_activity_instances {
        Participant participant;

        @BeforeEach
        void setup() {
            instance1 = instanceRepository.save(InstanceFactory.createActivity(10));
            participant = participantRepository.save(ParticipantFactory.createPreActivity(user, instance1));
        }

        @Nested
        @DisplayName("패스 아이템 사용 여부 조회할 때")
        class describe_check_pass_item {
            Item item;
            Orders orders;

            @BeforeEach
            void setup() {
                item = itemRepository.findAllByCategory(CERTIFICATION_PASSER).get(0);
                orders = ordersRepository.save(StoreFactory.createOrders(user, item, CERTIFICATION_PASSER, 3));
            }

            @Test
            @DisplayName("인증 정보가 DB에 저장되어 있지 않다면 사용 가능 여부가 true여야 한다.")
            public void it_return_true_when_certification_not_saved_DB() {
                List<ActivatedResponse> activatedInstances = myChallengeFacade.getActivatedInstances(user, localDate);
                for (ActivatedResponse activatedInstance : activatedInstances) {
                    assertThat(activatedInstance.isCanUsePassItem()).isTrue();
                }
            }

            @Test
            @DisplayName("인증 정보가 NOT_YET 이라면 사용 가능 여부가 true여야 한다.")
            public void it_return_true_when_certification_NOT_YET() {
                certificationRepository.save(CertificationFactory.createNotYet(participant, localDate));

                List<ActivatedResponse> activatedInstances = myChallengeFacade.getActivatedInstances(user, localDate);
                for (ActivatedResponse activatedInstance : activatedInstances) {
                    assertThat(activatedInstance.isCanUsePassItem()).isTrue();
                }
            }

            @ParameterizedTest
            @DisplayName("인증 정보가 PASSED 혹은 CERTIFICATED라면 사용 가능 여부가 false여야 한다.")
            @EnumSource(mode = Mode.INCLUDE, names = {"PASSED", "CERTIFICATED"})
            public void it_return_false_when_certification_PASSED_or_CERTIFICATED(CertificateStatus certificateStatus) {
                certificationRepository.save(CertificationFactory.create(certificateStatus, localDate, participant));

                List<ActivatedResponse> activatedInstances = myChallengeFacade.getActivatedInstances(user, localDate);
                for (ActivatedResponse activatedInstance : activatedInstances) {
                    assertThat(activatedInstance.isCanUsePassItem()).isFalse();
                }
            }
        }
    }

    @Nested
    @DisplayName("완료된 인스턴스 조회 시")
    class context_inquiry_done_instances {
        Participant participant;

        @BeforeEach
        void setup() {
            instance1 = instanceRepository.save(InstanceFactory.createDone(10));
        }

        @Nested
        @DisplayName("아직 보상받지 않은 인스턴스가 있을 때")
        class describe_not_rewarded_challenge_exist {
            @BeforeEach
            void setup() {
                participant = participantRepository.save(
                        ParticipantFactory.createByRewardStatus(user, instance1, SUCCESS, NO));
            }

            @Test
            @DisplayName("실패한 챌린지라면 획득 포인트는 0이어야하고, 달성률 정보를 전달해야 한다.")
            public void it_returns_achievementRate() {
                List<DoneResponse> doneInstances = myChallengeFacade.getDoneInstances(user, localDate);
                for (DoneResponse doneInstance : doneInstances) {
                    assertThat(doneInstance.getRewardedPoints()).isEqualTo(0);
                    assertThat(doneInstance.getAchievementRate()).isEqualTo(0.0);
                }
            }

            @Test
            @DisplayName("성공한 챌린지라면 보상 가능 여부가 true어야 한다.")
            public void it_return_true_when_success_instances() {
                List<DoneResponse> doneInstances = myChallengeFacade.getDoneInstances(user, localDate);
                for (DoneResponse doneInstance : doneInstances) {
                    assertThat(doneInstance.isCanGetReward()).isTrue();
                }
            }
        }

        @Nested
        @DisplayName("보상이 완료된 인스턴스가 있을 때")
        class describe_rewarded_challenge_exist {
            @BeforeEach
            void setup() {
                participant = participantRepository.save(
                        ParticipantFactory.createByRewardStatus(user, instance1, SUCCESS, YES));
            }

            @Test
            @DisplayName("획득 포인트와 달성률에 대한 정보를 전달해야 한다.")
            public void it_returns_point_and_achievementRate() {
                List<DoneResponse> doneInstances = myChallengeFacade.getDoneInstances(user, localDate);
                DoneResponse doneResponse = doneInstances.get(0);
                assertThat(doneResponse.getRewardedPoints()).isEqualTo(participant.getRewardPoints());
            }
        }
    }

    @Nested
    @DisplayName("챌린지 보상을 받을 때")
    class context_ {
        Participant participant;

        @BeforeEach
        void setup() {
            instance1 = instanceRepository.save(InstanceFactory.createDone(10));
        }

        @Nested
        @DisplayName("보상을 받을 수 있는 조건인지 확인했을 때")
        class describe_validate_reward_condition {
            @Test
            @DisplayName("JoinResult가 SUCCESS가 아니라면 CAN_NOT_GET_REWARDS 예외가 발생해야 한다.")
            public void it_throws_CAN_NOT_GET_REWARDS_exception() {
                participant = participantRepository.save(
                        ParticipantFactory.createByRewardStatus(user, instance1, FAIL, NO));
                assertThatThrownBy(() -> myChallengeFacade.getRewards(
                        RewardRequest.of(user.getId(), instance1.getId(), localDate)))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.CAN_NOT_GET_REWARDS.getMessage());
            }

            @Test
            @DisplayName("RewardStatus가 YES라면 ALREADY_REWARDED 예외가 발생해야 한다.")
            public void it_throws_ALREADY_REWARDED_exception() {
                participant = participantRepository.save(
                        ParticipantFactory.createByRewardStatus(user, instance1, SUCCESS, YES)
                );
                assertThatThrownBy(() -> myChallengeFacade.getRewards(
                        RewardRequest.of(user.getId(), instance1.getId(), localDate)
                ))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.ALREADY_REWARDED.getMessage());
            }
        }

        @Nested
        @DisplayName("보상을 받을 수 있고, 보상을 받았을 때")
        class describe_get_rewards {
            @BeforeEach
            void setup() {
                participant = participantRepository.save(
                        ParticipantFactory.createByRewardStatus(user, instance1, SUCCESS, NO)
                );
                myChallengeFacade.getRewards(RewardRequest.of(user.getId(), instance1.getId(), localDate));
            }

            @Test
            @DisplayName("participant의 RewardStatus가 YES가 되어야 한다.")
            public void it_change_rewardStatus_YES() {
                assertThat(participant.getRewardStatus()).isEqualTo(YES);
            }

            @Test
            @DisplayName("user의 point의 값이 갱신되어야 한다.")
            public void it_change_user_point() {
                assertThat(user.getPoint()).isEqualTo(participant.getRewardPoints());
            }
        }
    }
}