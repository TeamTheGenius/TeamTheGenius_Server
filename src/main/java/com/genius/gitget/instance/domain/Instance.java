package com.genius.gitget.instance.domain;

import com.genius.gitget.file.domain.Files;
import com.genius.gitget.hits.domain.Hits;
import com.genius.gitget.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.topic.domain.Topic;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_id")
    private Files files;

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
    // @Column(columnDefinition = "varchar(255) default 'PRE_ACTIVITY'")
    private Progress progress;

    @Column(name = "started_at")
    private LocalDateTime startedDate;

    @Column(name = "completed_at")
    private LocalDateTime completedDate;

    @Builder
    public Instance(String title, String description, String tags, int pointPerPerson, Progress progress,
                    LocalDateTime startedDate, LocalDateTime completedDate) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.pointPerPerson = pointPerPerson;
        this.progress = progress;
        this.startedDate = startedDate;
        this.completedDate = completedDate;
    }

    public void updateInstance(String description, int pointPerPerson, LocalDateTime startedDate,
                               LocalDateTime completedDate) {
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
    
    public void setFiles(Files files) {
        this.files = files;
    }
}
