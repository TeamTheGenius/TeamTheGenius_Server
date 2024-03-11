package com.genius.gitget.challenge.item.dto;

import lombok.Data;

@Data
public class ItemResponse {
    private Long itemId;
    private String name;
    private int cost;
    private int count;

    public ItemResponse(Long itemId, String name, int cost) {
        this.itemId = itemId;
        this.name = name;
        this.cost = cost;
    }

    public ItemResponse(Long itemId, String name, int cost, int count) {
        this.itemId = itemId;
        this.name = name;
        this.cost = cost;
        this.count = count;
    }
}
