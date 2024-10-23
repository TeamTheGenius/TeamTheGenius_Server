package com.genius.gitget.global.file.dto;

import lombok.Builder;

/**
 * UpdateDTO는 Files 객체의 갱신에 필요한 값들을 담는 객체입니다.
 *
 * @param originalFilename 사용자로부터 받은 이미지의 이름
 *                         (ex: sky.jpeg)
 * @param savedFilename    각 이미지를 식별하기 위해 UUID를 부여하여 만든 이미지의 이름
 *                         (ex:10ab2c6f-77d7-435e-96f0-e75b67213528.jpeg)
 * @param fileURI          이미지가 저장되는 경로
 *                         (ex: /Users/seonghuiyeon/GitGet/images/topic/10ab2c6f-77d7-435e-96f0-e75b67213528.jpeg)
 */
@Builder
public record UpdateDTO(
        String originalFilename,
        String savedFilename,
        String fileURI
) {

    public static UpdateDTO of(FileDTO fileDTO) {
        return UpdateDTO.builder()
                .originalFilename(fileDTO.originalFilename())
                .savedFilename(fileDTO.savedFilename())
                .fileURI(fileDTO.fileURI())
                .build();
    }
}
