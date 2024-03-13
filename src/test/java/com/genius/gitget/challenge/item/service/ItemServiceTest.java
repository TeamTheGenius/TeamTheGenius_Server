package com.genius.gitget.challenge.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import com.genius.gitget.challenge.item.dto.ItemResponse;
import com.genius.gitget.challenge.item.repository.ItemRepository;
import com.genius.gitget.challenge.item.repository.UserItemRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
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
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserItemRepository userItemRepository;

    @Test
    @DisplayName("데이터베이스에 저장되어 있는 모든 아이템 정보들을 받아올 수 있다.")
    public void should_getAllItems_when_itemsSaved() {
        //given
        User user = getSavedUser();

        //when
        List<ItemResponse> items = itemService.getAllItems(user);

        //then
        assertThat(items.size()).isEqualTo(3);
    }

    @ParameterizedTest
    @DisplayName("카테고리에 해당하는 아이템들을 받아올 수 있다.")
    @EnumSource(ItemCategory.class)
    public void should_getItems_when_passCategory(ItemCategory itemCategory) {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(itemCategory);
        UserItem userItem = getSavedUserItem(user, item, itemCategory);

        //when
        List<ItemResponse> itemResponses = itemService.getItemsByCategory(user, itemCategory);

        //then
        for (ItemResponse itemResponse : itemResponses) {
            assertThat(itemResponse.getName()).isEqualTo(itemCategory.getName());
        }
    }

    @ParameterizedTest
    @DisplayName("사용자의 포인트가 충분할 때, itemId(PK)를 전달하여 아이템을 구매할 수 있다.")
    @EnumSource(mode = Mode.EXCLUDE, names = {"PROFILE_FRAME"})
    public void should_purchaseItem_when_passPK(ItemCategory itemCategory) {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(itemCategory);

        user.updatePoints(1000L);

        //when
        ItemResponse itemResponse = itemService.orderItem(user, item.getId());

        //then
        assertThat(itemResponse.getItemId()).isEqualTo(item.getId());
        assertThat(itemResponse.getName()).isEqualTo(item.getName());
        assertThat(itemResponse.getCost()).isEqualTo(item.getCost());
        assertThat(itemResponse.getCount()).isEqualTo(2);
    }

    @ParameterizedTest
    @DisplayName("사용자의 포인트가 충분하지 않을 때, 아이템 구매를 시도하면 예외가 발생해야 한다.")
    @EnumSource(ItemCategory.class)
    public void should_throwException_when_pointNotEnough(ItemCategory itemCategory) {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(itemCategory);

        //when & then
        assertThatThrownBy(() -> itemService.orderItem(user, item.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NOT_ENOUGH_POINT.getMessage());
    }

    @Test
    @DisplayName("프로필 프레임을 재구매시도할 경우 예외가 발생해야 한다.")
    public void should_throwException_when_tryOrderFrameAgain() {
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);

        user.updatePoints(1000L);

        //when & then
        assertThatThrownBy(() -> itemService.orderItem(user, item.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ALREADY_PURCHASED.getMessage());
    }

    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("identifier")
                        .build()
        );
    }

    private Item getSavedItem(ItemCategory itemCategory) {
        return itemRepository.save(Item.builder()
                .itemCategory(itemCategory)
                .cost(100)
                .name(itemCategory.getName())
                .build());
    }

    private UserItem getSavedUserItem(User user, Item item, ItemCategory itemCategory) {
        UserItem userItem = UserItem.createDefault(1, itemCategory);
        userItem.setUser(user);
        userItem.setItem(item);
        return userItemRepository.save(userItem);
    }
}