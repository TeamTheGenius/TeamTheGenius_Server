package com.genius.gitget.challenge.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.store.item.service.OrdersProvider;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class OrdersProviderTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrdersProvider ordersProvider;

    @Test
    @DisplayName("사용자가 특정 아이템을 보유하고 있을 때, 보유하고 있는 아이템의 개수를 반환받을 수 있다.")
    public void should_returnItemCount_when_haveItem() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        getSavedOrder(user, item, 1);

        //when
        int numOfItem = ordersProvider.countNumOfItem(user, item.getId());

        //then
        assertThat(numOfItem).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자의 아이템 보유 정보가 DB에 저장되어있지 않을 때, 보유하고 있는 아이템의 개수를 요청하면 0을 반환한다.")
    public void should_returnZero_when_dataNotSaved() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);

        //when
        int numOfItem = ordersProvider.countNumOfItem(user, item.getId());

        //then
        assertThat(numOfItem).isEqualTo(0);
    }

    @Test
    @DisplayName("아이템을 구매한 경우 User PK와 Item PK를 통해 주문 내역을 받아올 수 있다.")
    public void should_getOrder_when_ordered() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        Orders orders = getSavedOrder(user, item, 1);

        //when
        Optional<Orders> optionalOrders = ordersProvider.findOptionalByOrderInfo(user.getId(), item.getId());

        //then
        assertThat(optionalOrders).isPresent();
        assertThat(optionalOrders.get()).isEqualTo(orders);
    }

    @Test
    @DisplayName("아이템을 구매하지 않은 경우 주문 내역을 받아왔을 때 Optional.null을 반환한다.")
    public void should_returnOptional_when_notOrdered() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);

        //when
        Optional<Orders> optionalOrders = ordersProvider.findOptionalByOrderInfo(user.getId(), item.getId());

        //then
        assertThat(optionalOrders).isNotPresent();
    }

    @Test
    @DisplayName("구매를 한 경우, 구매 아이템의 장착 상황을 얻을 수 있다.")
    public void should_getEquipStatus_when_ordered() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        getSavedOrder(user, item, 1);

        //when
        EquipStatus equipStatus = ordersProvider.getEquipStatus(user.getId(), item.getId());

        //then
        assertThat(equipStatus).isEqualTo(EquipStatus.AVAILABLE);
    }

    @Test
    @DisplayName("아이템 구매를 하지 않은 경우, 장착 상황을 반환받을 때 UNAVAILABLE을 반환한다.")
    public void should_returnUnavailable_when_notOrdered() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);

        //when
        EquipStatus equipStatus = ordersProvider.getEquipStatus(user.getId(), item.getId());

        //then
        assertThat(equipStatus).isEqualTo(EquipStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("사용자가 장착하고 있는 프로필 프레임이 하나 있을 때, 해당 프레임 아이템을 반환한다.")
    public void should_returnPK_when_equipOneFrame() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        Orders orders = getSavedOrder(user, item, 1);

        //when
        Item usingFrame = ordersProvider.getUsingFrameItem(user.getId());

        //then
        assertThat(item.getItemCategory()).isEqualTo(usingFrame.getItemCategory());
    }

    @Test
    @DisplayName("오류로 인해 사용자가 장착하고 있는 프로필 프레임이 두 개 이상일 때, 예외를 발생한다.")
    public void should_throwException_when_numOfFrameMoreThanTwo() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        Orders orders1 = getSavedOrder(user, item, 1);
        Orders orders2 = getSavedOrder(user, item, 1);

        orders1.updateEquipStatus(EquipStatus.IN_USE);
        orders2.updateEquipStatus(EquipStatus.IN_USE);

        //when & then
        assertThatThrownBy(() -> ordersProvider.getUsingFrameItem(user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.TOO_MANY_USING_FRAME.getMessage());
    }

    @Test
    @DisplayName("사용자가 장착하고 있는 프레임이 없을 때, 더미 데이터를 전달받는다.")
    public void should_returnDummy_when_notEquipped() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);

        //when
        Item usingFrame = ordersProvider.getUsingFrameItem(user.getId());

        //then
        assertThat(usingFrame.getId()).isNull();
    }


    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("githubId")
                        .information("information")
                        .tags("BE,FE")
                        .build()
        );
    }

    private Item getSavedItem(ItemCategory itemCategory) {
        return itemRepository.save(
                Item.builder()
                        .itemCategory(itemCategory)
                        .build()
        );
    }

    private Orders getSavedOrder(User user, Item item, int count) {
        Orders orders = Orders.createDefault(count, item.getItemCategory());
        orders.setUser(user);
        orders.setItem(item);
        return ordersRepository.save(orders);
    }
}