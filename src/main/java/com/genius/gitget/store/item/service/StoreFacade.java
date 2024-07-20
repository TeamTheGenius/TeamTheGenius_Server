package com.genius.gitget.store.item.service;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.dto.ItemUseResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import java.time.LocalDate;
import java.util.List;

public interface StoreFacade {
    List<ItemResponse> getItemsByCategory(User user, ItemCategory itemCategory);

    ItemResponse orderItem(User user, Long itemId);

    ItemUseResponse useItem(User user, Long itemId, Long instanceId, LocalDate currentDate);

    ItemUseResponse useFrameItem(Long userId, Orders orders);

    ItemUseResponse usePasserItem(Orders orders, Long instanceId, LocalDate currentDate);

    ItemUseResponse useMultiplierItem(Orders orders, Long instanceId, LocalDate currentDate);

    List<ProfileResponse> unmountFrame(User user);
}
