package com.genius.gitget.challenge.participant.domain;

import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "participant")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id")
    private Instance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "participant")
    private List<Certification> certificationList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @NotNull
    @ColumnDefault("'YES'")
    private JoinStatus joinStatus;

    @Enumerated(EnumType.STRING)
    private JoinResult joinResult;

    private String repositoryName;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NO'")
    private RewardStatus rewardStatus;

    private int rewardPoints;

    @Builder
    private Participant(JoinStatus joinStatus, JoinResult joinResult, String repositoryName) {
        this.joinStatus = joinStatus;
        this.joinResult = joinResult;
        this.repositoryName = repositoryName;
    }

    public static Participant createDefaultParticipant(String repositoryName) {
        return Participant.builder()
                .joinStatus(JoinStatus.YES)
                .joinResult(JoinResult.PROCESSING)
                .repositoryName(repositoryName)
                .build();
    }

    //=== 비지니스 로직 ===//
    public void joinChallenge() {
        this.joinStatus = JoinStatus.YES;
        this.joinResult = JoinResult.PROCESSING;
    }

    public void quitChallenge() {
        this.joinStatus = JoinStatus.NO;
        this.joinResult = JoinResult.FAIL;
    }

    public void getRewards(int rewardPoints) {
        this.rewardStatus = RewardStatus.YES;
        this.rewardPoints = rewardPoints;
    }

    public void updateRepository(String repository) {
        this.repositoryName = repository;
    }

    public LocalDate getStartedDate() {
        return this.getInstance().getStartedDate().toLocalDate();
    }


    /*== 연관관계 편의 메서드 ==*/
    public void setUserAndInstance(User user, Instance instance) {
        addParticipantInfoForUser(user);
        addParticipantInfoForInstance(instance);
    }

    private void addParticipantInfoForUser(User user) {
        this.user = user;
        if (!(user.getParticipantList().contains(this))) {
            user.getParticipantList().add(this);
        }
    }

    private void addParticipantInfoForInstance(Instance instance) {
        this.instance = instance;
        if (!(instance.getParticipantList().contains(this))) {
            instance.getParticipantList().add(this);
        }
    }
}
