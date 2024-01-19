package com.genius.gitget.instance.domain;

import com.genius.gitget.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.hits.domain.Hits;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "instance")
public class Instance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instance_id")
    private Long id;

    @OneToMany(mappedBy = "instance")
    private List<Hits> hitsList = new ArrayList<>();

    @OneToMany(mappedBy = "instance")
    private List<ParticipantInfo> participantInfoList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private String title;

    private String description;

    private int participants;

    private String tags;

    private int point_per_person;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PRE_ACTIVITY'")
    private Progress progress;

    @Column(name = "started_at")
    private LocalDateTime startedDate;

    @Column(name = "completed_at")
    private LocalDateTime completedDate;


    public Instance(String title, String description, int participants, String tags, int point_per_person, Progress progress, LocalDateTime startedDate, LocalDateTime completedDate) {
        this.title = title;
        this.description = description;
        this.participants = participants;
        this.tags = tags;
        this.point_per_person = point_per_person;
        this.progress = progress;
        this.startedDate = startedDate;
        this.completedDate = completedDate;
    }

    //== 연관관계 편의 메서드 ==//
    public void setTopic(Topic topic) {
        this.topic = topic;
        if (!topic.getInstanceList().contains(this)) {
            topic.getInstanceList().add(this);
        }
    }
}
