package com.genius.todoffin.user.entity;


import com.genius.todoffin.common.BaseTimeEntity;
import com.genius.todoffin.security.constants.ProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Enumerated(EnumType.STRING)
    private ProviderType provider;
    @NotNull
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(unique = true, length = 16)
    private String nickname;

    @Column(length = 160)
    private String information;
    private String interest;


    @Builder
    public User(ProviderType provider, String email, Role role, String nickname, String information,
                String interest) {
        this.provider = provider;
        this.email = email;
        this.role = role;
        this.nickname = nickname;
        this.information = information;
        this.interest = interest;
    }


    public String getAuthorities() {
        return role.getKey();
    }
}
