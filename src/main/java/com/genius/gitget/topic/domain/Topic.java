package com.genius.gitget.topic.domain;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.domain.FileHolder;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.util.domain.BaseTimeEntity;
import jakarta.persistence.CascadeType;
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
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "topic")
public class Topic extends BaseTimeEntity implements FileHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "files_id")
    private Files files;

    @OneToMany(mappedBy = "topic")
    private List<Instance> instanceList = new ArrayList<>();

    private String title;

    private String description;

    private String tags;

    private String notice;

    private int pointPerPerson;


    @Builder
    public Topic(String title, String description, String tags, String notice, int pointPerPerson) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.notice = notice;
        this.pointPerPerson = pointPerPerson;
    }

    public void updateExistInstance(String description) {
        this.description = description;
    }

    public void updateNotExistInstance(String title, String description, String tags, String notice,
                                       int pointPerPerson) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.notice = notice;
        this.pointPerPerson = pointPerPerson;
    }

    @Override
    public Optional<Files> getFiles() {
        return Optional.ofNullable(this.files);
    }

    @Override
    public void setFiles(Files files) {
        this.files = files;
    }
}