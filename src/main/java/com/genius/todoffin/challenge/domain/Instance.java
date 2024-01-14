package com.genius.todoffin.challenge.domain;

import com.genius.todoffin.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Instance extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instance_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @Comment("토픽 PK")
    private Topic topic;

    @OneToMany
    @JoinColumn(name = "hits_id")
    private List<Hits> hitsList = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "participantInfo_id")
    private List<ParticipantInfo> participantInfoList = new ArrayList<>();

    private String title;

    private String description;

    private int participants;

    private String tags;

    private int point_per_person;

    private int like_count;

    @Enumerated(EnumType.STRING)
    private Progress progress;

    public Instance(String title, String description, int participants, String tags, int point_per_person, int like_count, Progress progress) {
        this.title = title;
        this.description = description;
        this.participants = participants;
        this.tags = tags;
        this.point_per_person = point_per_person;
        this.like_count = like_count;
        this.progress = progress;
    }
}
