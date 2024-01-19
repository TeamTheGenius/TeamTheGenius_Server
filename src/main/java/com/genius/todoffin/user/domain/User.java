package com.genius.todoffin.user.domain;

import com.genius.todoffin.hits.domain.Hits;
import com.genius.todoffin.participantinfo.domain.ParticipantInfo;
import com.genius.todoffin.common.domain.BaseTimeEntity;
import com.genius.todoffin.security.constants.ProviderType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
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

    @Column(unique = true, length = 10)
    private String nickname;

    private String interest;

    @Column(length = 100)
    private String information;


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


    @OneToMany(mappedBy = "user")
    private List<Hits> hitsList = new ArrayList<>();


    @OneToMany(mappedBy = "user")
    private List<ParticipantInfo> participantInfoList = new ArrayList<>();



}
