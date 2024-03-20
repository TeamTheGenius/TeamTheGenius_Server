package com.genius.gitget.store.item.dto;

import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import lombok.Data;

@Data
public class ItemResponse {
    private Long itemId;
    private ItemCategory itemCategory;
    private String name;
    private String details;
    private int cost;
    private int count;

    protected ItemResponse(Item item, int count) {
        this.itemId = item.getId();
        this.itemCategory = item.getItemCategory();
        this.name = item.getName();
        this.details = item.getDetails();
        this.cost = item.getCost();
        this.count = count;
    }

    public static ItemResponse create(Item item, int count) {
        return new ItemResponse(item, count);
    }
}
