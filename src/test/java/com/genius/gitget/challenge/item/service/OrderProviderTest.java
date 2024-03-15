package com.genius.gitget.challenge.item.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.Order;
import com.genius.gitget.challenge.item.repository.ItemRepository;
import com.genius.gitget.challenge.item.repository.OrderRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class OrderProviderTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProvider orderProvider;

    @Test
    @DisplayName("사용자가 특정 아이템을 보유하고 있을 때, 보유하고 있는 아이템의 개수를 반환받을 수 있다.")
    public void should_returnItemCount_when_haveItem() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        getSavedOrder(user, item, 1);

        //when
        int numOfItem = orderProvider.countNumOfItem(user, item.getId());

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
        int numOfItem = orderProvider.countNumOfItem(user, item.getId());

        //then
        assertThat(numOfItem).isEqualTo(0);
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

    private Order getSavedOrder(User user, Item item, int count) {
        Order order = Order.createDefault(count, item.getItemCategory());
        order.setUser(user);
        order.setItem(item);
        return orderRepository.save(order);
    }
}