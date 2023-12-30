package com.genius.todoffin.user.entity;


import com.genius.todoffin.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    private String provider;
    @NotNull
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    @Column(unique = true, length = 16)
    private String nickname;

    @Column(length = 160)
    private String information;
    private String interest;


    // 임시
    private String password;


    @Builder
    public User(String provider, String email, Role role, String nickname, String information,
                String interest, String password) {
        this.provider = provider;
        this.email = email;
        this.role = role;
        this.nickname = nickname;
        this.information = information;
        this.interest = interest;
        this.password = password;
    }

}
