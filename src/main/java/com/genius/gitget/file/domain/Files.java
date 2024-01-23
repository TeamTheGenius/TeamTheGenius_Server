package com.genius.gitget.file.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    //TODO: User 연관관계 설정 필요(Profile)
    //TODO: Topic 연관관계 설정 필요
    //TODO: Instance 연관관계 설정 필요
    //TODO: 추후 PET쪽과 연관관계 설정 필요

    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    private String originalFilename;

    private String savedFilename;

    private String fileURI;

    @Builder
    public Files(FileType fileType, String originalFilename, String savedFilename, String fileURI) {
        this.fileType = fileType;
        this.originalFilename = originalFilename;
        this.savedFilename = savedFilename;
        this.fileURI = fileURI;
    }
}
