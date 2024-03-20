package com.genius.gitget.store.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemUseResponse {
    Long itemId;

    public ItemUseResponse() {
    }

    public ItemUseResponse(Long itemId) {
        this.itemId = itemId;
    }
}
