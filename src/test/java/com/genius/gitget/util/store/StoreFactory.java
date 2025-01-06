package com.genius.gitget.util.store;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;

public class StoreFactory {
    public static Item createItem(ItemCategory itemCategory) {
        return Item.builder()
                .identifier(10)
                .itemCategory(itemCategory)
                .cost(100)
                .name(itemCategory.getName())
                .details("details")
                .build();
    }

    public static Orders createOrders(User user, Item item, ItemCategory itemCategory, int count) {
        Orders orders = Orders.of(count, itemCategory);
        orders.setItem(item);
        orders.setUser(user);
        return orders;
    }
}
