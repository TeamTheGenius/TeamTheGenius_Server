package com.genius.gitget.store.item.dto;

import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.Orders;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponse extends ItemResponse {
    private String equipStatus;

    public ProfileResponse(Item item, int numOfItem, String equipStatus) {
        super(item, numOfItem);
        this.equipStatus = equipStatus;
    }

    public static ProfileResponse create(Item item, int numOfItem, String equipStatus) {
        return new ProfileResponse(item, numOfItem, equipStatus);
    }

    public static ProfileResponse createByEntity(Orders orders) {
        return create(orders.getItem(), orders.getCount(), orders.getEquipStatus().getTag());
    }
}
