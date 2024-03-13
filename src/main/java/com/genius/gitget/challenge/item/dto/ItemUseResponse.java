package com.genius.gitget.challenge.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemUseResponse {
    private Long instanceId;
    private String title;
    private int pointPerPerson;

    public ItemUseResponse(Long instanceId, String title, int pointPerPerson) {
        this.instanceId = instanceId;
        this.title = title;
        this.pointPerPerson = pointPerPerson;
    }
}
