package com.genius.todoffin.participantinfo.domain;

import com.genius.todoffin.instance.domain.Instance;
import com.genius.todoffin.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "participantInfo")
public class ParticipantInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participantInfo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id")
    private Instance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Enumerated(EnumType.STRING)
    @Column(name = "join_status")
    @NotNull
    @ColumnDefault("'YES'")
    private JoinStatus joinStatus;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PROCESSING'")
    @NotNull
    @Column(name = "join_result")
    private JoinResult joinResult;

    @Builder
    public ParticipantInfo(JoinStatus joinStatus, JoinResult joinResult) {
        this.joinStatus = joinStatus;
        this.joinResult = joinResult;
    }

    /*== 연관관계 편의 메서드 ==*/
    public void setUserAndInstance(User user, Instance instance) {
        addParticipantInfoForUser(user);
        addParticipantInfoForInstance(instance);
        setUser(user);
        setInstance(instance);
    }

    private void setUser(User user) {
        this.user = user;
    }

    private void addParticipantInfoForUser(User user) {
        if(!(user.getParticipantInfoList().contains(this))) {
            user.getParticipantInfoList().add(this);
        }
    }

    private void setInstance(Instance instance) {
        this.instance = instance;
    }

    private void addParticipantInfoForInstance(Instance instance) {
        if(!(instance.getParticipantInfoList().contains(this))) {
            instance.getParticipantInfoList().add(this);
        }
    }
}
