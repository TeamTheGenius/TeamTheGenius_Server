package com.genius.gitget.challenge.user.domain;

import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.global.file.domain.FileHolder;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.domain.BaseTimeEntity;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.payment.domain.Payment;
import jakarta.persistence.CascadeType;
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
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity implements FileHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "files_id")
    private Files files;

    @OneToMany(mappedBy = "user")
    private List<Likes> likesList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participantList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payment = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orders> ordersList = new ArrayList<>();

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

    private String tags;

    @Column(length = 100)
    private String information;

    @Column(columnDefinition = "TEXT")
    private String githubToken;

    @ColumnDefault(value = "0")
    private Long point = 0L;

    @Builder
    public User(ProviderInfo providerInfo, String identifier, Role role, String nickname, String information,
                String tags) {
        this.providerInfo = providerInfo;
        this.identifier = identifier;
        this.role = role;
        this.nickname = nickname;
        this.tags = tags;
        this.information = information;
    }

    //=== 비지니스 로직 ===//
    public void updateUserInformation(String nickname, String information) {
        this.nickname = nickname;
        this.information = information;
    }

    public void updateUserTags(String tags) {
        this.tags = tags;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updateGithubPersonalToken(String encryptedToken) {
        this.githubToken = encryptedToken;
    }

    public long updatePoints(Long amount) {
        this.point += amount;
        return this.point;
    }

    public boolean isRegistered() {
        return this.role != Role.NOT_REGISTERED;
    }

    @Override
    public Optional<Files> getFiles() {
        return Optional.ofNullable(this.files);
    }

    @Override
    public void setFiles(Files files) {
        this.files = files;
    }

    //=== 연관관계 편의 메서드 ===//

    public void updateUser(String nickname, String information, String tags) {
        this.nickname = nickname;
        this.information = information;
        this.tags = tags;
    }

    public void deleteLikesList() {
        this.likesList.clear();
    }
}
