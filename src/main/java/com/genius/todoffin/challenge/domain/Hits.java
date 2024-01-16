package com.genius.todoffin.challenge.domain;

import com.genius.todoffin.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hits")
public class Hits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hits_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Instance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private User user;
}
