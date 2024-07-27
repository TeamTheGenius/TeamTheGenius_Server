package com.genius.gitget.store.facade;

import static com.genius.gitget.global.util.exception.ErrorCode.ALREADY_REWARDED;
import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_GET_REWARDS;
import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_USE_PASS_ITEM;
import static com.genius.gitget.store.item.domain.ItemCategory.CERTIFICATION_PASSER;
import static com.genius.gitget.store.item.domain.ItemCategory.POINT_MULTIPLIER;
import static com.genius.gitget.store.item.domain.ItemCategory.PROFILE_FRAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.domain.RewardStatus;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.EquipStatus;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import com.genius.gitget.store.item.facade.StoreFacade;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.util.certification.CertificationFactory;
import com.genius.gitget.util.instance.InstanceFactory;
import com.genius.gitget.util.participant.ParticipantFactory;
import com.genius.gitget.util.store.StoreFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
class StoreFacadeTest {
    private User user;
    private LocalDate currentDate = LocalDate.now();

    @Autowired
    private StoreFacade storeFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private CertificationRepository certificationRepository;


    @BeforeEach
    void setup() {
        user = userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("identifier")
                        .build()
        );

    }

    @Nested
    @DisplayName("아이템 목록 조회 시")
    class describe_get_item_list {
        @Nested
        @DisplayName("카테고리 별로 조회를 하면")
        class context_inquiry_by_category {
            @ParameterizedTest
            @DisplayName("카테고리에 해당하는 아이템들을 받아올 수 있다.")
            @EnumSource(ItemCategory.class)
            public void it_returns_item_list(ItemCategory itemCategory) {
                Item item = itemRepository.save(StoreFactory.createItem(itemCategory));
                ordersRepository.save(StoreFactory.createOrders(user, item, itemCategory, 1));

                List<ItemResponse> itemResponses = storeFacade.getItemsByCategory(user, itemCategory);

                for (ItemResponse itemResponse : itemResponses) {
                    assertThat(itemResponse.getName()).contains(itemCategory.getName());
                    assertThat(itemResponse.getDetails()).isNotBlank();
                }
            }
        }
    }

    @Nested
    @DisplayName("아이템 구매 시")
    class describe_purchase_item {
        @Nested
        @DisplayName("사용자의 포인트가 충분하다면")
        class context_user_have_enough_point {
            @ParameterizedTest
            @DisplayName("아이템을 구매할 수 있다.")
            @EnumSource(ItemCategory.class)
            public void it_returns_200(ItemCategory itemCategory) {
                Item item = itemRepository.save(StoreFactory.createItem(itemCategory));
                user.updatePoints(1000L);

                ItemResponse itemResponse = storeFacade.orderItem(user, item.getId());

                assertThat(itemResponse.getItemId()).isEqualTo(item.getIdentifier());
                assertThat(itemResponse.getName()).isEqualTo(item.getName());
                assertThat(itemResponse.getCost()).isEqualTo(item.getCost());
                assertThat(itemResponse.getCount()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("사용자의 포인트가 충분하지 않다면")
        class context_user_have_not_enough_point {
            @ParameterizedTest
            @DisplayName("NOT_ENOUGH_POINT 예외가 발생한다.")
            @EnumSource(ItemCategory.class)
            public void it_throws_NOT_ENOUGH_POINT_exception(ItemCategory itemCategory) {
                Item item = itemRepository.save(StoreFactory.createItem(itemCategory));

                assertThatThrownBy(() -> storeFacade.orderItem(user, item.getId()))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.NOT_ENOUGH_POINT.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("아이템 사용 시")
    class describe_use_item {
        Item item;
        Instance instance;

        @BeforeEach
        void setup() {
            instance = instanceRepository.save(InstanceFactory.createPreActivity(10));
        }

        @Nested
        @DisplayName("카테고리에 상관없이 Orders의 정보를 DB에서 조회했을 때")
        class context_inquiry_orders {
            @ParameterizedTest
            @DisplayName("정보는 존재하지만 count가 0개 이하일 때, HAS_NO_ITEM 예외를 발생한다")
            @EnumSource(ItemCategory.class)
            public void it_throws_HAS_NO_ITEM_exception(ItemCategory itemCategory) {
                item = itemRepository.save(StoreFactory.createItem(itemCategory));
                ordersRepository.save(StoreFactory.createOrders(user, item, itemCategory, 0));

                assertThatThrownBy(() -> storeFacade.useItem(user, item.getId(), instance.getId(), currentDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.HAS_NO_ITEM.getMessage());
            }

            @ParameterizedTest
            @DisplayName("정보가 존재하지 않을 때 ORDERS_NOT_FOUND 예외가 발생한다.")
            @EnumSource(ItemCategory.class)
            public void it_throws_ORDERS_NOT_FOUND_exception(ItemCategory itemCategory) {
                item = itemRepository.save(StoreFactory.createItem(itemCategory));

                assertThatThrownBy(() -> storeFacade.useItem(user, item.getId(), instance.getId(), currentDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.ORDERS_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("인증 패스 아이템 사용 시")
    class describe_use_certification_pass_item {
        Instance instance;
        Participant participant;
        Item item;
        Orders orders;

        @BeforeEach
        void setup() {
            item = itemRepository.save(StoreFactory.createItem(CERTIFICATION_PASSER));
            orders = ordersRepository.save(StoreFactory.createOrders(user, item, CERTIFICATION_PASSER, 5));
        }

        @Nested
        @DisplayName("아이템을 가지고 있고, 인증 상태를 조회했을 때")
        class context_has_item_and_check_certification_status {
            @BeforeEach
            void setup() {
                instance = instanceRepository.save(InstanceFactory.createActivity(10));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
            }

            @Test
            @DisplayName("인증 정보가 존재하지 않는 경우 아이템을 사용할 수 있다.")
            public void it_returns_200_when_certification_not_exist() {
                int holding = orders.getCount();
                storeFacade.useItem(user, item.getId(), instance.getId(), currentDate);
                assertThat(orders.getCount()).isEqualTo(holding - 1);
            }

            @Test
            @DisplayName("인증 정보는 있으나 NOT_YET인 경우 아이템을 사용할 수 있다.")
            public void it_returns_200_when_certification_is_NOT_YET() {
                int holding = orders.getCount();
                certificationRepository.save(
                        CertificationFactory.create(CertificateStatus.NOT_YET, currentDate, participant)
                );
                storeFacade.useItem(user, item.getId(), instance.getId(), currentDate);

                assertThat(orders.getCount()).isEqualTo(holding - 1);
            }

            @ParameterizedTest
            @DisplayName("인증 정보는 있으나 CERTIFICATED 혹은 PASSED 라면 예외가 발생한다.")
            @EnumSource(mode = Mode.INCLUDE, names = {"CERTIFICATED", "PASSED"})
            public void it_throws_exception_status_is_CERTIFICATED_or_PASSED(CertificateStatus certificateStatus) {
                certificationRepository.save(
                        CertificationFactory.create(certificateStatus, currentDate, participant)
                );
                assertThatThrownBy(() -> storeFacade.useItem(user, item.getId(), instance.getId(), currentDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(CAN_NOT_USE_PASS_ITEM.getMessage());
            }
        }

        @Nested
        @DisplayName("아이템을 가지고 있고, 인스턴스 상태를 조회했을 때")
        class context_has_item_and_check_instance_status {
            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY가 아니라면 NOT_ACTIVITY_INSTANCE 예외가 발생한다.")
            public void it_throws_exception_when_instance_not_ACTIVITY() {
                instance = instanceRepository.save(InstanceFactory.createPreActivity(10));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                assertThatThrownBy(() -> storeFacade.useItem(user, item.getId(), instance.getId(), currentDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.NOT_ACTIVITY_INSTANCE.getMessage());
            }

            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY라면 아이템을 사용할 수 있다.")
            public void it_returns_200_instance_status_is_ACTIVITY() {
                int holding = orders.getCount();
                instance = instanceRepository.save(InstanceFactory.createActivity(10));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                storeFacade.useItem(user, item.getId(), instance.getId(), currentDate);

                assertThat(orders.getCount()).isEqualTo(holding - 1);
            }
        }
    }

    @Nested
    @DisplayName("포인트 2배 획득 아이템 사용 시")
    class describe_use_point_multiplier_item {
        Instance instance;
        Participant participant;
        Item item;
        Orders orders;

        @BeforeEach
        void setup() {
            item = itemRepository.save(StoreFactory.createItem(POINT_MULTIPLIER));
            orders = ordersRepository.save(StoreFactory.createOrders(user, item, POINT_MULTIPLIER, 5));
        }

        @Nested
        @DisplayName("아이템을 가지고 있고, 인스턴스의 상태를 조회했을 때")
        class context_has_item_and_check_instance_status {
            @Test
            @DisplayName("인스턴스의 상태가 DONE이 아니라면 예외가 발생한다.")
            public void it_throws_exception_when_instance_not_DONE() {
                instance = instanceRepository.save(InstanceFactory.createActivity(10));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));

                assertThatThrownBy(() -> storeFacade.useItem(user, item.getId(), instance.getId(), currentDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(CAN_NOT_GET_REWARDS.getMessage());
            }

            @Test
            @DisplayName("인스턴스의 상태가 DONE이라면 아이템을 사용할 수 있다.")
            public void it_returns_200_when_instance_is_DONE() {
                int holding = orders.getCount();
                instance = instanceRepository.save(InstanceFactory.createDone(10));
                participant = participantRepository.save(
                        ParticipantFactory.createByRewardStatus(user, instance, RewardStatus.NO));

                storeFacade.useItem(user, item.getId(), instance.getId(), currentDate);

                assertThat(orders.getCount()).isEqualTo(holding - 1);
            }
        }

        @Nested
        @DisplayName("사용 가능 여부를 확인했을 때")
        class context_check_valid_to_use_item {
            @BeforeEach
            void setup() {
                instance = instanceRepository.save(InstanceFactory.createDone(10));
            }

            @ParameterizedTest
            @DisplayName("participant의 JoinResult가 SUCCESS가 아니라면 CAN_NOT_GET_REWARDS 예외가 발생한다.")
            @EnumSource(mode = Mode.INCLUDE, names = {"PROCESSING", "FAIL"})
            public void it_throws_exception_when_JoinResult_not_SUCCESS(JoinResult joinResult) {
                participant = participantRepository.save(
                        ParticipantFactory.createByJoinResult(user, instance, joinResult));
                assertThatThrownBy(() -> storeFacade.useItem(user, item.getId(), instance.getId(), currentDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(CAN_NOT_GET_REWARDS.getMessage());
            }

            @Test
            @DisplayName("participant의 RewardStatus가 YES라면 ALREADY_REWARDED 예외가 발생한다.")
            public void it_throws_exception_when_RewardStatus_is_YES() {
                participant = participantRepository.save(
                        ParticipantFactory.createByRewardStatus(user, instance, RewardStatus.YES)
                );
                assertThatThrownBy(() -> storeFacade.useItem(user, item.getId(), instance.getId(), currentDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ALREADY_REWARDED.getMessage());
            }
        }

        @Nested
        @DisplayName("아이템을 사용해서 아이템의 개수가 0이 되었을 때")
        class context_item_count_is_zero {
            @BeforeEach
            void setup() {
                instance = instanceRepository.save(InstanceFactory.createDone(10));
                participant = participantRepository.save(
                        ParticipantFactory.createByRewardStatus(user, instance, RewardStatus.NO));
            }

            @Test
            @DisplayName("Orders 정보가 DB에서 삭제된다.")
            public void it_delete_Orders_from_DB() {
                int holding = 1;
                orders = ordersRepository.save(StoreFactory.createOrders(user, item, POINT_MULTIPLIER, holding));
                storeFacade.useMultiplierItem(orders, instance.getId(), currentDate);

                Optional<Orders> optionalOrders = ordersRepository.findById(orders.getId());
                assertThat(optionalOrders).isNotPresent();
            }
        }
    }

    @Nested
    @DisplayName("프로필 아이템 사용 시")
    class describe_use_profile_item {
        Item item;
        Orders orders;

        @BeforeEach
        void setup() {
            item = itemRepository.save(StoreFactory.createItem(PROFILE_FRAME));
            orders = ordersRepository.save(StoreFactory.createOrders(user, item, PROFILE_FRAME, 2));
        }

        @Nested
        @DisplayName("사용 가능 여부를 확인했을 때")
        class context_check_valid_to_use_item {
            @Test
            @DisplayName("기존에 사용 중인 프로필 아이템이 있는 경우 TOO_MANY_USING_FRAME 예외가 발생한다.")
            public void it_throws_exception_already_using_frame_exist() {
                orders.updateEquipStatus(EquipStatus.IN_USE);
                assertThatThrownBy(() -> storeFacade.useFrameItem(user.getId(), orders))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.TOO_MANY_USING_FRAME.getMessage());
            }

            @Test
            @DisplayName("EquipStatus가 UNAVAILABLE인 경우 INVALID_EQUIP_CONDITION 예외가 발생한다.")
            public void it_throws_exception_when_equipStatus_is_unavailable() {
                orders.updateEquipStatus(EquipStatus.UNAVAILABLE);
                assertThatThrownBy(() -> storeFacade.useFrameItem(user.getId(), orders))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.INVALID_EQUIP_CONDITION.getMessage());
            }

            @Test
            @DisplayName("EquipStatus가 IN_USE인 경우 INVALID_EQUIP_CONDITION 예외가 발생한다.")
            public void it_throws_exception_when_equipStatus_is_in_use() {
                orders.updateEquipStatus(EquipStatus.IN_USE);
                assertThatThrownBy(() -> storeFacade.useFrameItem(user.getId(), orders))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.TOO_MANY_USING_FRAME.getMessage());
            }

            @Test
            @DisplayName("조건에 부합한다면 프로필 아이템을 사용할 수 있다.")
            public void it_returns_200_when_condition_match() {
                storeFacade.useFrameItem(user.getId(), orders);
                assertThat(orders.getEquipStatus()).isEqualTo(EquipStatus.IN_USE);
            }
        }
    }

    @Nested
    @DisplayName("아이템 장착 해제 요청 시")
    class describe_unmount_item {
        Item item;
        Orders orders;

        @Nested
        @DisplayName("프로필 아이템이 아니라면")
        class context_not_profile_item {
            @ParameterizedTest
            @DisplayName("응답 데이터가 포함되지 않는다.")
            @EnumSource(mode = Mode.INCLUDE, names = {"POINT_MULTIPLIER", "CERTIFICATION_PASSER"})
            public void it_not_contain_response_data(ItemCategory itemCategory) {
                item = itemRepository.save(StoreFactory.createItem(itemCategory));
                ordersRepository.save(StoreFactory.createOrders(user, item, itemCategory, 2));

                List<ProfileResponse> profileResponses = storeFacade.unmountFrame(user);
                assertThat(profileResponses.size()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("프로필 아이템인 경우")
        class context_profile_item {
            @Test
            @DisplayName("EquipStatus가 IN_USE라면 응답 데이터가 포함된다.")
            public void it_contains_response_data_equipStatus_is_IN_USE() {
                item = itemRepository.save(StoreFactory.createItem(PROFILE_FRAME));
                orders = ordersRepository.save(StoreFactory.createOrders(user, item, PROFILE_FRAME, 2));
                orders.updateEquipStatus(EquipStatus.IN_USE);

                List<ProfileResponse> profileResponses = storeFacade.unmountFrame(user);
                assertThat(profileResponses.size()).isEqualTo(1);
            }

            @ParameterizedTest
            @DisplayName("EquipStatus가 IN_USE가 아니라면 응답 데이터가 포함되지 않는다.")
            @EnumSource(mode = Mode.INCLUDE, names = {"UNAVAILABLE", "AVAILABLE"})
            public void it_not_contains_response_data_equipStatus_not_IN_USE(EquipStatus equipStatus) {
                item = itemRepository.save(StoreFactory.createItem(PROFILE_FRAME));
                orders = ordersRepository.save(StoreFactory.createOrders(user, item, PROFILE_FRAME, 2));
                orders.updateEquipStatus(equipStatus);

                List<ProfileResponse> profileResponses = storeFacade.unmountFrame(user);
                assertThat(profileResponses.size()).isEqualTo(0);
            }
        }
    }
}