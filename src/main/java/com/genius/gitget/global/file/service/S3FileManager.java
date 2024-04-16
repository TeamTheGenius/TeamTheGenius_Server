package com.genius.gitget.global.file.service;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileDTO;
import com.genius.gitget.global.file.dto.UpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

/**
 * !!!이 클래스의 모든 주석은 삭제해도 무방합니다!!!
 * <p>
 * S3 bucket에 이미지를 업로드하는 코드를 구현하는 곳
 * 파일 시스템 구조 확장에 참고한 링크
 * https://chb2005.tistory.com/200#3.5.%20%ED%8C%8C%EC%9D%BC%20%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C%20%EA%B5%AC%ED%98%84
 * https://docs.aws.amazon.com/ko_kr/sdk-for-java/v1/developer-guide/examples-s3-objects.html#upload-object
 */
@RequiredArgsConstructor
public class S3FileManager implements FileManager {
    private final FileUtil fileUtil;

    @Override
    public UrlResource download(Files files) {
        return null;
    }

    @Override
    public FileDTO upload(MultipartFile multipartFile, FileType fileType) {
        return null;
    }

    @Override
    public FileDTO copy(Files files, FileType fileType) {
        return null;
    }

    @Override
    public UpdateDTO update(Files files, MultipartFile multipartFile) {
        return null;
    }

    @Override
    public void deleteInStorage(Files files) {
    }
}
