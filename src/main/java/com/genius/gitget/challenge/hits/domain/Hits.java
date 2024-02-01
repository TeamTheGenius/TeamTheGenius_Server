package com.genius.gitget.challenge.hits.domain;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hits")
@EntityListeners(AuditingEntityListener.class)
public class Hits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hits_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id")
    private Instance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @CreatedDate
    @Column(name = "liked_at", unique = false)
    private LocalDateTime likedAt; // 찜하기 누른 시각

    public Hits(User user, Instance instance) {
        this.instance = instance;
        this.user = user;
        setUserAndInstance(user, instance);
    }

    /*== 연관관계 편의 메서드 ==*/
    public void setUserAndInstance(User user, Instance instance) {
        addHitsForUser(user);
        addHitsForInstance(instance);
    }

    private void addHitsForUser(User user) {
        if (!(user.getHitsList().contains(this))) {
            user.getHitsList().add(this);
        }
        this.user = user;
    }

    private void addHitsForInstance(Instance instance) {
        if (!(instance.getHitsList().contains(this))) {
            instance.getHitsList().add(this);
        }
        this.instance = instance;
    }
}