package com.genius.gitget.instance.domain;

import com.genius.gitget.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.hits.domain.Hits;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
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

    private String tags;

    private int pointPerPerson;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PRE_ACTIVITY'")
    private Progress progress;

    @Column(name = "started_at")
    private LocalDateTime startedDate;

    @Column(name = "completed_at")
    private LocalDateTime completedDate;

    @Builder
    public Instance(String title, String description, String tags, int pointPerPerson, Progress progress, LocalDateTime startedDate, LocalDateTime completedDate) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.pointPerPerson = pointPerPerson;
        this.progress = progress;
        this.startedDate = startedDate;
        this.completedDate = completedDate;
    }

    public void updateInstance(String description, int pointPerPerson, LocalDateTime startedDate, LocalDateTime completedDate) {
        this.description = description;
        this.pointPerPerson = pointPerPerson;
        this.startedDate = startedDate;
        this.completedDate = completedDate;
    }

    public int getJoinPeopleCount() {
        return participantInfoList.size();
    }

    //== 연관관계 편의 메서드 ==//
    public void setTopic(Topic topic) {
        this.topic = topic;
        if (!topic.getInstanceList().contains(this)) {
            topic.getInstanceList().add(this);
        }
    }
}
