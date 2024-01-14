package com.genius.todoffin.user.domain;

import com.genius.todoffin.challenge.domain.Hits;
import com.genius.todoffin.challenge.domain.ParticipantInfo;
import com.genius.todoffin.common.domain.BaseTimeEntity;
import com.genius.todoffin.security.constants.ProviderType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private ProviderType providerInfo;

    @NotNull
    private String identifier;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    @Column(unique = true, length = 10)
    private String nickname;

    @NotNull
    private String interest;

    @Column(length = 100)
    private String information;

    @OneToMany
    @JoinColumn(name = "hits_id")
    private List<Hits> hitsList = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "participantInfo_id")
    private List<ParticipantInfo> participantInfoList = new ArrayList<>();


    @Builder
    public User(ProviderType providerInfo, String identifier, Role role, String nickname, String interest, String information) {
        this.providerInfo = providerInfo;
        this.identifier = identifier;
        this.role = role;
        this.nickname = nickname;
        this.interest = interest;
        this.information = information;
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
