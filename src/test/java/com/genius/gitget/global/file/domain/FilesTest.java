package com.genius.gitget.global.file.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.global.file.dto.UpdateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FilesTest {

    @Test
    @DisplayName("파일이 수정되어야 할 때, UpdateDTO를 전달하여 정보를 수정할 수 있다.")
    public void should_updateFiles_when_passUpdateDTO() {
        //given
        Files files = Files.builder()
                .fileType(FileType.INSTANCE)
                .originalFilename("originalFilename")
                .savedFilename("savedFilename")
                .fileURI("accessURI")
                .build();

        UpdateDTO updateDTO = UpdateDTO.builder()
                .savedFilename("new savedFilename")
                .originalFilename("new originalFilename")
                .fileURI("new accessURI")
                .build();

        //when
        files.updateFiles(updateDTO);

        //then
        assertThat(files.getOriginalFilename()).isEqualTo(updateDTO.originalFilename());
        assertThat(files.getSavedFilename()).isEqualTo(updateDTO.savedFilename());
        assertThat(files.getFileURI()).isEqualTo(updateDTO.fileURI());

    }
}