package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_EXIST;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.CopyDTO;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.file.dto.UploadDTO;
import com.genius.gitget.global.file.repository.FilesRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FilesService {
    private final String UPLOAD_PATH;
    private final FileManager fileManager;
    private final FilesRepository filesRepository;

    public FilesService(@Value("${file.upload.path}") String UPLOAD_PATH, FileManager fileManager,
                        FilesRepository filesRepository) {
        this.UPLOAD_PATH = UPLOAD_PATH;
        this.fileManager = fileManager;
        this.filesRepository = filesRepository;
    }


    // TODO: instance 대상으로 사용하는 부분
    // instance 생성 요청을 했을 때, 전달받은 MultipartFile이 없는 경우 토픽의 MultipartFile을 사용해야 함
    @Transactional
    public Files uploadFile(Optional<Files> optionalFiles, MultipartFile receivedFile, FileType fileType) {
        if (receivedFile != null) {
            return uploadFile(receivedFile, fileType);
        }

        Files files = optionalFiles.orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));
        CopyDTO copyDTO = fileManager.copy(files, FileType.INSTANCE);

        //REFACTOR: 정적 팩토리 메서드로 처리하면 깔끔할 듯!
        Files copyFiles = Files.builder()
                .originalFilename(copyDTO.originalFilename())
                .savedFilename(copyDTO.savedFilename())
                .fileType(copyDTO.fileType())
                .fileURI(copyDTO.fileURI())
                .build();
        
        return filesRepository.save(copyFiles);
    }

    @Transactional
    public Files uploadFile(MultipartFile multipartFile, FileType fileType) {
        UploadDTO uploadDTO = fileManager.upload(multipartFile, fileType);

        Files file = Files.builder()
                .originalFilename(uploadDTO.originalFilename())
                .savedFilename(uploadDTO.savedFilename())
                .fileType(uploadDTO.fileType())
                .fileURI(uploadDTO.fileURI())
                .build();

        return filesRepository.save(file);
    }

    @Transactional
    public Files updateFile(Long fileId, MultipartFile multipartFile) {
        Files files = filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));

        if (multipartFile == null) {
            return files;
        }

        UpdateDTO updateDTO = fileManager.update(files, multipartFile);
        files.updateFiles(updateDTO);
        return files;
    }

    /**
     * NOTE: 삭제하고자하는 Files 엔티티와 연관관계에 있는 엔티티에서 연관관계를 끊어줘야 합니다.
     *
     * @param fileId 삭제하고자하는 Files 엔티티의 PK
     */
    @Transactional
    public void deleteFile(Long fileId) {
        Files files = filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));

        fileManager.deleteInStorage(files);
        filesRepository.delete(files);
    }

    public FileResponse getEncodedFile(Long fileId) {
        Optional<Files> optionalFiles = filesRepository.findById(fileId);
        return optionalFiles
                .map(FileResponse::createExistFile)
                .orElseGet(FileResponse::createNotExistFile);

    }

    public FileResponse getEncodedFile(Optional<Files> optionalFiles) {
        return optionalFiles
                .map(FileResponse::createExistFile)
                .orElseGet(FileResponse::createNotExistFile);
    }
}
