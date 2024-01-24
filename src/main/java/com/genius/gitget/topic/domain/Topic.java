package com.genius.gitget.topic.domain;

import com.genius.gitget.file.domain.Files;
import com.genius.gitget.instance.domain.Instance;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "topic")

public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_id")
    private Files files;

    @OneToMany(mappedBy = "topic")
    private List<Instance> instanceList = new ArrayList<>();

    private String title;

    private String description;

    private String tags;

    private int pointPerPerson;


    @Builder
    public Topic(String title, String description, String tags, int pointPerPerson) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.pointPerPerson = pointPerPerson;
    }

    //== 비즈니스 로직 ==//
    public void updateExistInstance(String description) {
        this.description = description;
    }

    public void createInstance(String title, String description, String tags, int pointPerPerson) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.pointPerPerson = pointPerPerson;
    }

    //== 연관관계 편의 메서드 ==//
    public void setInstance(Instance instance) {
        instanceList.add(instance);
        if (instance.getTopic() != this) {
            instance.setTopic(this);
        }
    }
    
    public void setFiles(Files files) {
        this.files = files;
    }
}