package com.genius.gitget.file.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.file.domain.FileType;
import com.genius.gitget.file.domain.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FilesRepositoryTest {
    @Autowired
    private FilesRepository filesRepository;

    @Test
    @DisplayName("File 엔티티 저장한 후, PK를 통해 해당 엔티티를 찾을 수 있다.")
    public void fileSaveTest() {
        //given
        Files files = Files.builder()
                .fileType(FileType.TOPIC)
                .savedFilename("saved file name")
                .originalFilename("original file name")
                .fileURI("file uri")
                .build();

        //when
        Files savedFiles = filesRepository.save(files);

        //then
        assertThat(savedFiles.getFileType()).isEqualTo(files.getFileType());
        assertThat(savedFiles.getSavedFilename()).isEqualTo(files.getSavedFilename());
        assertThat(savedFiles.getOriginalFilename()).isEqualTo(files.getOriginalFilename());
        assertThat(savedFiles.getFileURI()).isEqualTo(files.getFileURI());
    }

}