package com.genius.gitget.challenge.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.repository.ItemRepository;
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
class ItemProviderTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemProvider itemProvider;

    @ParameterizedTest
    @DisplayName("DB에 저장되어 있는 아이템을 카테고리 별로 받아올 수 있다.")
    @EnumSource(mode = Mode.INCLUDE, names = {"POINT_MULTIPLIER", "CERTIFICATION_PASSER"})
    public void should_findItems_when_passCategory(ItemCategory itemCategory) {
        //given
        Item item = getSavedItem(itemCategory);

        //when
        List<Item> items = itemProvider.findAllByCategory(itemCategory);

        //then
        assertThat(items.size()).isEqualTo(2);
        Item foundItem = items.get(0);
        assertThat(foundItem.getItemCategory()).isEqualTo(item.getItemCategory());
    }

    @Test
    @DisplayName("DB에 저장되어 있는 아이템을 식별자 PK를 통해 조회할 수 있다.")
    public void should_findItem_when_passPK() {
        //given
        Item item = getSavedItem(ItemCategory.CERTIFICATION_PASSER);

        //when
        Item foundItem = itemProvider.findById(item.getId());

        //then
        assertThat(item.getId()).isEqualTo(foundItem.getId());
        assertThat(item.getItemCategory()).isEqualTo(foundItem.getItemCategory());
        assertThat(item.getCost()).isEqualTo(foundItem.getCost());
    }

    @Test
    @DisplayName("PK를 통해 아이템을 조회하려고 했을 때, 존재하지 않으면 예외를 발생시켜야 한다.")
    public void should_throwException_when_pkNotExist() {
        assertThatThrownBy(() -> itemProvider.findById(0L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ITEM_NOT_FOUND.getMessage());
    }

    private Item getSavedItem(ItemCategory itemCategory) {
        return itemRepository.save(
                Item.builder()
                        .cost(100)
                        .itemCategory(itemCategory)
                        .build()
        );
    }
}