package com.genius.gitget.store.item.domain;

import com.genius.gitget.global.util.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @OneToMany(mappedBy = "item")
    private List<Orders> ordersList = new ArrayList<>();

    @Column(unique = true)
    private Integer identifier;

    private String name;

    private int cost;

    @Enumerated(EnumType.STRING)
    private ItemCategory itemCategory;

    private String details;

    @Builder
    public Item(String name, int cost, Integer identifier,
                ItemCategory itemCategory, String details) {
        this.name = name;
        this.cost = cost;
        this.identifier = identifier;
        this.itemCategory = itemCategory;
        this.details = details;
    }
}
