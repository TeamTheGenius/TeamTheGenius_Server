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

    public List<ItemResponse> getProfileList(User user) {
        List<ItemResponse> profileResponses = new ArrayList<>();
        List<Item> items = itemProvider.findAllByCategory(ItemCategory.PROFILE_FRAME);

        for (Item item : items) {
            EquipStatus equipStatus = userItemProvider.getEquipStatus(user.getId(), ItemCategory.PROFILE_FRAME);

            ProfileResponse profileResponse = ProfileResponse.create(item, equipStatus.getTag());
            profileResponses.add(profileResponse);
        }

        return profileResponses;
    }


    public List<ItemResponse> getItemsByCategory(User user, ItemCategory itemCategory) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        List<Item> items = itemProvider.findAllByCategory(itemCategory);

        for (Item item : items) {
            int count = userItemProvider.countNumOfItem(user, itemCategory);

            ItemResponse itemResponse = ItemResponse.create(item, count);
            itemResponses.add(itemResponse);
        }

        return itemResponses;
    }
}
