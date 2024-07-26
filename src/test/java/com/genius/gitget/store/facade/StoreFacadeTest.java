package com.genius.gitget.store.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.facade.StoreFacade;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.util.instance.InstanceFactory;
import com.genius.gitget.util.store.StoreFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class StoreFacadeTest {
    private User user;

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
        LocalDate localDate;
        Item item;
        Instance instance;

        @BeforeEach
        void setup() {
            localDate = LocalDate.now();
            instance = instanceRepository.save(InstanceFactory.create());
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

                assertThatThrownBy(() -> storeFacade.useItem(user, item.getId(), instance.getId(), localDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.HAS_NO_ITEM.getMessage());
            }

            @ParameterizedTest
            @DisplayName("정보가 존재하지 않을 때 ORDERS_NOT_FOUND 예외가 발생한다.")
            @EnumSource(ItemCategory.class)
            public void it_throws_ORDERS_NOT_FOUND_exception(ItemCategory itemCategory) {
                item = itemRepository.save(StoreFactory.createItem(itemCategory));

                assertThatThrownBy(() -> storeFacade.useItem(user, item.getId(), instance.getId(), localDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.HAS_NO_ITEM.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("인증 패스 아이템 사용 시")
    class describe_use_certification_pass_item {
        @Nested
        @DisplayName("아이템을 가지고 있고, 인증 상태를 조회했을 때")
        class context_has_item_and_check_certification_status {
            @Test
            @DisplayName("인증 정보가 존재하지 않는 경우 아이템을 사용할 수 있다.")
            public void it_returns_200_when_certification_not_exist() {
                //given

                //when

                //then

            }

            @Test
            @DisplayName("인증 정보는 있으나 NOT_YET인 경우 아이템을 사용할 수 있다.")
            public void it_returns_200_when_certification_is_NOT_YET() {
                //given

                //when

                //then

            }

            @Test
            @DisplayName("인증 정보는 있으나 CERTIFICATED 혹은 PASSED 라면 예외가 발생한다.")
            public void it_throws_exception_status_is_CERTIFICATED_or_PASSED() {
                //given

                //when

                //then

            }
        }

        @Nested
        @DisplayName("아이템을 가지고 있고, 인스턴스 상태를 조회했을 때")
        class context_has_item_and_check_instance_status {
            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY가 아니라면 NOT_ACTIVITY_INSTANCE 예외가 발생한다.")
            public void it_throws_exception_when_instance_not_ACTIVITY() {
                //given

                //when

                //then

            }

            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY라면 아이템을 사용할 수 있다.")
            public void it_returns_200_instance_status_is_ACTIVITY() {
                //given

                //when

                //then

            }
        }
    }

    @Nested
    @DisplayName("포인트 2배 획득 아이템 사용 시")
    class describe_use_point_multiplier_item {
        @Nested
        @DisplayName("아이템을 가지고 있고, 인스턴스의 상태를 조회했을 때")
        class context_has_item_and_check_instance_status {
            @Test
            @DisplayName("인스턴스의 상태가 DONE이 아니라면 예외가 발생한다.")
            public void it_throws_exception_when_instance_not_DONE() {
                //given

                //when

                //then

            }

            @Test
            @DisplayName("인스턴스의 상태가 DONE이라면 아이템을 사용할 수 있다.")
            public void it_returns_200_when_instance_is_DONE() {
                //given

                //when

                //then

            }
        }

        @Nested
        @DisplayName("사용 가능 여부를 확인했을 때")
        class context_check_valid_to_use_item {
            @Test
            @DisplayName("JoinResult가 SUCCESS가 아니라면 CAN_NOT_GET_REWARDS 예외가 발생한다.")
            public void it_throws_exception_when_JoinResult_not_SUCCESS() {
                //given

                //when

                //then

            }

            @Test
            @DisplayName("RewardStatus가 YES라면 ALREADY_REWARDED 예외가 발생한다.")
            public void it_throws_exception_when_RewardStatus_is_YES() {
                //given

                //when

                //then

            }
        }

        @Nested
        @DisplayName("아이템을 사용해서 아이템의 개수가 0이 되었을 때")
        class context_item_count_is_zero {
            @Test
            @DisplayName("Orders 정보가 DB에서 삭제된다.")
            public void it_delete_Orders_from_DB() {
                //given

                //when

                //then

            }
        }
    }

    @Nested
    @DisplayName("프로필 아이템 사용 시")
    class describe_use_profile_item {
        @Nested
        @DisplayName("사용 가능 여부를 확인했을 때")
        class context_check_valid_to_use_item {
            @Test
            @DisplayName("기존에 사용 중인 프로필 아이템이 있는 경우 TOO_MANY_USING_FRAME 예외가 발생한다.")
            public void it_throws_exception_already_using_frame_exist() {
                //given

                //when

                //then

            }

            @Test
            @DisplayName("프로필 아이템 Orders 정보가 없는 경우 HAS_NO_ITEM 예외가 발생한다.")
            public void it_throws_exception_when_dont_have_profile_item() {
                //given

                //when

                //then

            }

            @Test
            @DisplayName("EquipStatus가 AVAILABLE이 아닌 경우 INVALID_EQUIP_CONDITION 예외가 발생한다.")
            public void it_throws_exception_when_equipStatus_not_available() {
                //given

                //when

                //then

            }

            @Test
            @DisplayName("조건에 부합한다면 프로필 아이템을 사용할 수 있다.")
            public void it_returns_200_when_condition_match() {
                //given

                //when

                //then

            }
        }
    }

    @Nested
    @DisplayName("아이템 장착 해제 요청 시")
    class describe_unmount_item {
        @Nested
        @DisplayName("프로필 아이템이 아니라면")
        class context_not_profile_item {
            @Test
            @DisplayName("응답 데이터가 포함되지 않는다.")
            public void it_not_contain_response_data() {
                //given

                //when

                //then

            }
        }

        @Nested
        @DisplayName("프로필 아이템인 경우")
        class context_profile_item {
            @Test
            @DisplayName("EquipStatus가 IN_USE라면 응답 데이터가 포함된다.")
            public void it_contains_response_data_equipStatus_is_IN_USE() {
                //given

                //when

                //then

            }

            @Test
            @DisplayName("EquipStatus가 IN_USE가 아니라면 응답 데이터가 포함되지 않는다.")
            public void it_not_contains_response_data_equipStatus_not_IN_USE() {
                //given

                //when

                //then

            }
        }
    }
}