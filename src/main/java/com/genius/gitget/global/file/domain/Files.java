package com.genius.gitget.global.file.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.genius.gitget.global.file.dto.FileDTO;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.util.domain.BaseTimeEntity;
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

    public static Files create(FileDTO fileDTO) {
        return Files.builder()
                .originalFilename(fileDTO.originalFilename())
                .savedFilename(fileDTO.savedFilename())
                .fileType(fileDTO.fileType())
                .fileURI(fileDTO.fileURI())
                .build();
    }

    //== 비지니스 로직 ==//
    public void updateFiles(UpdateDTO updateDTO) {
        this.originalFilename = updateDTO.originalFilename();
        this.savedFilename = updateDTO.savedFilename();
        this.fileURI = updateDTO.fileURI();
    }
}
