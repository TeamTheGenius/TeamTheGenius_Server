package com.genius.gitget.global.file.service;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.CopyDTO;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.file.dto.UploadDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public interface FileManager {
    String getEncodedImage(Files files);

    /**
     * 전달한 파일 저장 후, Files 객체 형성에 필요한 정보를 담은 객체 반환
     *
     * @param multipartFile 저장하고자 전달한 파일
     * @param fileType      저장하고자하는 파일의 종류 (Topic, Instance, Profile 중 1)
     * @return Files 객체 생성에 필요한 정보(UploadDTO) 반환
     */
    UploadDTO upload(MultipartFile multipartFile, FileType fileType);

    CopyDTO copy(Files files, FileType fileType);

    /**
     * @param files         대체 하고자하는 대상 객체
     * @param multipartFile 저장하고자하는 파일
     * @return Files 내용 갱신에 필요한 정보(UpdateDTO) 반환
     */
    UpdateDTO update(Files files, MultipartFile multipartFile);

    /**
     * Files 객체 내의 정보를 활용하여 저장소(Local/S3)에서 해당 파일 삭제.
     *
     * @param files 삭제하고자하는 Files 객체
     * @throws BusinessException 삭제에 실패했을 때 발생
     */
    void deleteInStorage(Files files);
}
