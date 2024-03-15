package com.genius.gitget.challenge.item.dto;

import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import lombok.Data;

@Data
public class ItemResponse {
    private Long itemId;
    private ItemCategory itemCategory;
    private String name;
    private int cost;
    private int count;

    protected ItemResponse(Item item, int count) {
        this.itemId = item.getId();
        this.itemCategory = item.getItemCategory();
        this.name = item.getName();
        this.cost = item.getCost();
        this.count = count;
    }

    public static ItemResponse create(Item item, int count) {
        return new ItemResponse(item, count);
    }
}
