package com.genius.gitget.challenge.item.domain;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class UserItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_item_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int count;

    public UserItem(int count) {
        this.count = count;
    }

    //=== 비지니스 로직 ===//
    public boolean hasItem() {
        return this.count > 0;
    }

    public void useItem() {
        if (!hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }
        this.count -= 1;
    }

    //=== 연관관계 편의 메서드 ===//
    public void setUser(User user) {
        this.user = user;
        if (!user.getUserItemList().contains(this)) {
            user.getUserItemList().add(this);
        }
    }

    public void setItem(Item item) {
        this.item = item;
        if (!item.getUserItemList().contains(this)) {
            item.getUserItemList().add(this);
        }
    }
}
