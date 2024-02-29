package com.genius.gitget.challenge.item.domain;

import com.genius.gitget.challenge.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
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
    @GeneratedValue
    Long userItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int count;


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
