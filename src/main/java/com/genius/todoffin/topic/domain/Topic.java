package com.genius.todoffin.topic.domain;

import com.genius.todoffin.instance.domain.Instance;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;

    @OneToMany(mappedBy = "topic")
    private List<Instance> instanceList;

    private String title;

    private String description;

    private String tags;

    private int point_per_person;

    public Topic(String title, String description, String tags, int point_per_person) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.point_per_person = point_per_person;
    }
}