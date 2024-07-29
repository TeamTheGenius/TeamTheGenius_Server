package com.genius.gitget.store.item.facade;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.dto.OrderResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import java.time.LocalDate;
import java.util.List;

public interface StoreFacade {

    List<ItemResponse> getItemsByCategory(User user, ItemCategory itemCategory);

    ItemResponse orderItem(User user, Long itemId);

    OrderResponse useItem(User user, Long itemId, Long instanceId, LocalDate currentDate);

    OrderResponse useFrameItem(Long userId, Orders orders);

    OrderResponse usePasserItem(Orders orders, Long instanceId, LocalDate currentDate);

    OrderResponse useMultiplierItem(Orders orders, Long instanceId, LocalDate currentDate);

    List<ProfileResponse> unmountFrame(User user);
}
