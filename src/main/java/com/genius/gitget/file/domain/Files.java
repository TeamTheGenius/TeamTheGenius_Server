package com.genius.gitget.file.domain;

import com.genius.gitget.common.domain.BaseTimeEntity;
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
public class Files extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "files_id")
    private Long id;

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
