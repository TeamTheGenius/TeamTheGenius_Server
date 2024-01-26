package com.genius.gitget.challenge.user.domain;

import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.challenge.hits.domain.Hits;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_id")
    private Files files;

    @OneToMany(mappedBy = "user")
    private List<Hits> hitsList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ParticipantInfo> participantInfoList = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProviderInfo providerInfo;

    @NotNull
    private String identifier;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true, length = 20)
    private String nickname;

    private String interest;

    @Column(length = 100)
    private String information;

    @Builder
    public User(ProviderInfo providerInfo, String identifier, Role role, String nickname, String information,
                String interest) {
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

    //=== 연관관계 편의 메서드 ===//
    public void setFiles(Files files) {
        this.files = files;
    }
}
