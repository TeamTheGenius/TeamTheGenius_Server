package com.genius.todoffin.challenge.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;

    private String title;

    private String description;

    private String tags;

    private int point_per_person;

    @OneToMany
    @JoinColumn(name = "instance_id")
    private List<Instance> instanceList;

    public Topic(String title, String description, String tags, int point_per_person) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.point_per_person = point_per_person;
    }
}
