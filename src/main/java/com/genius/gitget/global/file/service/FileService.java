package com.genius.gitget.global.file.service;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileDTO;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.util.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public interface FileService {

    /**
     * Files 내에 저장된 값들을 통해 UrlResource 등으로 다운받은 후, base64로 인코딩한 결과 반환
     *
     * @param files 얻기 원하는 파일의 정보를 담고 있는 Files 객체
     * @return base64로 encode한 결과 값(문자열) 반환
     * 파일을 받아오지 못한 경우에는 빈 문자열("") 반환
     */
    String getFileAccessURI(Files files);

    /**
     * 전달한 파일 저장 후, Files 객체 형성에 필요한 정보를 담은 객체 반환
     *
     * @param multipartFile 저장하고자 전달한 파일
     * @param fileType      저장하고자하는 파일의 종류 (Topic, Instance, Profile 중 1)
     * @return Files 객체 생성에 필요한 정보(UploadDTO) 반환
     */
    FileDTO upload(MultipartFile multipartFile, FileType fileType);

    /**
     * 기존에 저장소에 저장되어 있던 파일을 특정 타입에 복사 후, Files 객체 생성에 필요한 정보들을 반환
     * NOTE!! 복사 이전에 원본이 되는 파일이 저장소에 존재하는지 `validateFileExist()`를 통해 확인 필요
     *
     * @param files    복사하고자하는 파일의 정보를 담고 있는 Files 객체
     * @param fileType 복사해서 적용하고 싶은 대상의 파일 타입(TOPIC/INSTANCE/PROFILE 중 택 1)
     * @return Files 객체 생성에 필요한 정보(UploadDTO) 반환
     * @throws BusinessException 원본이 되는 파일이 저장소에 존재하지 않는 경우 FILE_NOT_EXIST 발생
     */
    FileDTO copy(Files files, FileType fileType);

    /**
     * Files에 해당하는 이미지를 찾아서 삭제 및 새로운 이미지 저장 후, Files 내용 갱신에 필요한 정보들을 반환
     *
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

    /**
     * Files 객체 내의 정보를 활용하여 저장소에 파일이 저장이 되어 있는지 확인 후 boolean 반환
     * 각 저장소의 특성에 맞춰 Files 내의 메타데이터를 통해 저장소 내에 파일이 제대로 저장되어 있는지 확인
     * 파일이 존재하지 않는 경우 FILE_NOT_EXIST 예외 발생 시킬 것
     *
     * @param files 저장소에 저장되어 있는지 확인하고자하는 Files 객체
     * @throws FILE_NOT_EXIST 저장소에서 파일(이미지)을 찾을 수 없을 때 발생
     */
    void validateFileExist(Files files);
}
