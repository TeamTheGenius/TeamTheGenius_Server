package com.genius.gitget.global.file.dto;

import com.genius.gitget.global.file.domain.FileType;
import lombok.Builder;

/**
 * FileDTO는 Files 객체 생성에 필요한 값들을 담어서 전달하는 역할을 합니다.
 *
 * @param fileType         저장하고자하는 이미지의 타입
 *                         (TOPIC, INSTANCE, PROFILE 중 택1)
 * @param originalFilename 사용자로부터 받은 이미지의 이름
 *                         (ex: sky.jpeg)
 * @param savedFilename    각 이미지를 식별하기 위해 UUID를 부여하여 만든 이미지의 이름
 *                         (ex:10ab2c6f-77d7-435e-96f0-e75b67213528.jpeg)
 * @param fileURI          이미지가 저장되는 경로
 *                         (ex: /Users/seonghuiyeon/GitGet/images/topic/10ab2c6f-77d7-435e-96f0-e75b67213528.jpeg)
 */
@Builder
public record FileDTO(FileType fileType,
                      String originalFilename,
                      String savedFilename,
                      String fileURI) {
}
