package com.genius.todoffin.participantinfo.domain;

import com.genius.todoffin.instance.domain.Instance;
import com.genius.todoffin.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "join_status")
    @ColumnDefault("'NO'")
    private JoinStatus joinStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_result")
    private JoinResult joinResult;

    public ParticipantInfo(JoinStatus joinStatus, JoinResult joinResult) {
        this.joinStatus = joinStatus;
        this.joinResult = joinResult;
    }
}
