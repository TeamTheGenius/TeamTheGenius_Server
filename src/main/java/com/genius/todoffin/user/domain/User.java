package com.genius.todoffin.user.domain;


import com.genius.todoffin.common.domain.BaseTimeEntity;
import com.genius.todoffin.security.constants.ProviderInfo;
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
    private ProviderInfo providerInfo;

    @NotNull
    private String identifier;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true, length = 16)
    private String nickname;

    @Column(length = 160)
    private String information;
    private String interest;


    @Builder
    public User(ProviderInfo providerInfo, String identifier, Role role, String nickname, String information,
                String interest) {
        this.providerInfo = providerInfo;
        this.identifier = identifier;
        this.role = role;
        this.nickname = nickname;
        this.information = information;
        this.interest = interest;
    }

    public void updateUser(String nickname, String information, String interest) {
        this.nickname = nickname;
        this.information = information;
        this.interest = interest;
    }

    public void updateRole(Role role) {
        this.role = role;
    }
}
