package com.genius.gitget.store.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {
    Long itemId;

    public OrderResponse() {
    }

    public OrderResponse(Long itemId) {
        this.itemId = itemId;
    }
}
