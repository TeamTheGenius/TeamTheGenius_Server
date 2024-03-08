package com.genius.gitget.challenge.likes.domain;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likes")
@EntityListeners(AuditingEntityListener.class)
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id")
    private Instance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @CreatedDate
    @Column(name = "liked_at")
    private LocalDateTime likedAt; // 찜하기 누른 시각

    public Likes(User user, Instance instance) {
        this.instance = instance;
        this.user = user;
        setUserAndInstance(user, instance);
    }

    /*== 연관관계 편의 메서드 ==*/
    public void setUserAndInstance(User user, Instance instance) {
        addLikesForUser(user);
        addLikesForInstance(instance);
    }

    private void addLikesForUser(User user) {
        if (!(user.getLikesList().contains(this))) {
            user.getLikesList().add(this);
        }
        this.user = user;
    }

    private void addLikesForInstance(Instance instance) {
        if (!(instance.getLikesList().contains(this))) {
            instance.getLikesList().add(this);
        }
        this.instance = instance;
    }
}
