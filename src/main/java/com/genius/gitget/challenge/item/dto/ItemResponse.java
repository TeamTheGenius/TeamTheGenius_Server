package com.genius.gitget.challenge.item.dto;

import com.genius.gitget.challenge.item.domain.Item;
import lombok.Data;

@Data
public class ItemResponse {
    private Long itemId;
    private String name;
    private int cost;
    private int count;

    protected ItemResponse(Long itemId, String name, int cost) {
        this.itemId = itemId;
        this.name = name;
        this.cost = cost;
    }

    private ItemResponse(Long itemId, String name, int cost, int count) {
        this.itemId = itemId;
        this.name = name;
        this.cost = cost;
        this.count = count;
    }

    public static ItemResponse create(Item item, int count) {
        return new ItemResponse(item.getId(), item.getName(), item.getCost(), count);
    }
}
