package com.genius.gitget.challenge.domain;

import com.genius.gitget.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "instance")
public class Instance extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @OneToMany(mappedBy = "instance")
    private List<Hits> hitsList = new ArrayList<>();

    @OneToMany(mappedBy = "instance")
    private List<ParticipantInfo> participantInfoList = new ArrayList<>();

    private String title;

    private String description;

    private int participants;

    private String tags;

    private int point_per_person;

    @NotNull
    @ColumnDefault("0")
    private int like_count;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PRE_ACTIVITY'")
    private Progress progress;

    public Instance(String title, String description, int participants, String tags, int point_per_person,
                    int like_count, Progress progress) {
        this.title = title;
        this.description = description;
        this.participants = participants;
        this.tags = tags;
        this.point_per_person = point_per_person;
        this.like_count = like_count;
        this.progress = progress;
    }
}
