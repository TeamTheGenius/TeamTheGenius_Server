package com.genius.todoffin.challenge.domain;

import com.genius.todoffin.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@RequiredArgsConstructor
public class ParticipantInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participantInfo_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "instance_id")
    @Comment("instance_FK")
    private Instance instance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Comment("user_FK")
    private User user;

    @Column(name = "join_status")
    private Boolean joinStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_result")
    private JoinResult joinResult;

    public ParticipantInfo(Boolean joinStatus, JoinResult joinResult) {
        this.joinStatus = joinStatus;
        this.joinResult = joinResult;
    }
}
