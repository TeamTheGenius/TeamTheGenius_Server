package com.genius.gitget.challenge.item.domain;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int count;

    @Enumerated(value = EnumType.STRING)
    private EquipStatus equipStatus;

    private Order(int count, EquipStatus equipStatus) {
        this.count = count;
        this.equipStatus = equipStatus;
    }

    public static Order createDefault(int count, ItemCategory itemCategory) {
        if (itemCategory == ItemCategory.PROFILE_FRAME) {
            return new Order(count, EquipStatus.AVAILABLE);
        }
        return new Order(count, EquipStatus.UNAVAILABLE);
    }

    //=== 비지니스 로직 ===//
    public boolean hasItem() {
        return this.count > 0;
    }

    public int purchase() {
        if (this.item.getItemCategory() == ItemCategory.PROFILE_FRAME && hasItem()) {
            throw new BusinessException(ErrorCode.ALREADY_PURCHASED);
        }
        this.count++;
        return count;
    }

    public void useItem() {
        if (!hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }
        this.count -= 1;
    }

    public void updateEquipStatus(EquipStatus equipStatus) {
        this.equipStatus = equipStatus;
    }

    //=== 연관관계 편의 메서드 ===//
    public void setUser(User user) {
        this.user = user;
        if (!user.getOrderList().contains(this)) {
            user.getOrderList().add(this);
        }
    }

    public void setItem(Item item) {
        this.item = item;
        if (!item.getOrderList().contains(this)) {
            item.getOrderList().add(this);
        }
    }
}
