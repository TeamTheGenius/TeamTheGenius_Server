package com.genius.gitget.challenge.item.dto;

import com.genius.gitget.challenge.item.domain.Item;
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
}
