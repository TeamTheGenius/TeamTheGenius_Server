package com.genius.gitget.challenge.item.service;

import com.genius.gitget.challenge.item.domain.EquipStatus;
import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.dto.ItemResponse;
import com.genius.gitget.challenge.item.dto.ProfileResponse;
import com.genius.gitget.challenge.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemProvider itemProvider;
    private final UserItemProvider userItemProvider;


    public List<ItemResponse> getItemsByCategory(User user, ItemCategory itemCategory) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        List<Item> items = itemProvider.findAllByCategory(itemCategory);

        for (Item item : items) {
            ItemResponse itemResponse = getItemResponse(user, item, itemCategory);
            itemResponses.add(itemResponse);
        }

        return itemResponses;
    }

    private ItemResponse getItemResponse(User user, Item item, ItemCategory itemCategory) {
        if (itemCategory == ItemCategory.PROFILE_FRAME) {
            EquipStatus equipStatus = userItemProvider.getEquipStatus(user.getId(), ItemCategory.PROFILE_FRAME);
            return ProfileResponse.create(item, equipStatus.getTag());
        }
        int count = userItemProvider.countNumOfItem(user, itemCategory);
        return ItemResponse.create(item, count);
    }
}
