package com.genius.todoffin.challenge.domain;

import com.genius.todoffin.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@RequiredArgsConstructor
public class Hits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hits_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "instance_id")
    @Comment("인스턴스 PK")
    private Instance instance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Comment("유저 PK")
    private User user;
}
