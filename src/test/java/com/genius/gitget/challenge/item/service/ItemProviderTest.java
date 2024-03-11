package com.genius.gitget.challenge.item.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.repository.ItemRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
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
    @EnumSource(mode = Mode.INCLUDE, names = {"POINT_MULTIPLIER", "CERTIFICATION_PASSER", "PROFILE_FRAME"})
    public void should_findItems_when_passCategory(ItemCategory itemCategory) {
        //given
        Item item = getSavedItem(itemCategory);

        //when
        List<Item> items = itemProvider.findAllByCategory(itemCategory);

        //then
        assertThat(items.size()).isEqualTo(1);
        Item foundItem = items.get(0);
        assertThat(foundItem.getId()).isEqualTo(item.getId());
        assertThat(foundItem.getItemCategory()).isEqualTo(item.getItemCategory());
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