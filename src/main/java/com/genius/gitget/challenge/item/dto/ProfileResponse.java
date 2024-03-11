package com.genius.gitget.challenge.item.dto;

import com.genius.gitget.challenge.item.domain.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponse extends ItemResponse {
    private String equipStatus;

    @Builder
    public ProfileResponse(Item item, String equipStatus) {
        super(item.getId(), item.getName(), item.getCost());
        this.equipStatus = equipStatus;
    }

    public static ProfileResponse create(Item item, String equipStatus) {
        return new ProfileResponse(item, equipStatus);
    }
}
