package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_EXIST;
import static com.genius.gitget.global.util.exception.ErrorCode.MULTIPART_FILE_NOT_EXIST;

import com.genius.gitget.global.file.domain.FileHolder;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileDTO;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.file.repository.FilesRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FilesManager {
    private final FileService fileService;
    private final FilesRepository filesRepository;


    @Transactional
    public Files uploadFile(MultipartFile multipartFile, FileType fileType) {
        FileDTO fileDTO = fileService.upload(multipartFile, fileType);

        Files file = Files.builder()
                .originalFilename(fileDTO.originalFilename())
                .savedFilename(fileDTO.savedFilename())
                .fileType(fileDTO.fileType())
                .fileURI(fileDTO.fileURI())
                .build();
        return filesRepository.save(file);
    }

    @Transactional
    public Files uploadFile(FileHolder fileHolder, MultipartFile multipartFile, FileType fileType) {
        if (multipartFile == null) {
            throw new BusinessException(MULTIPART_FILE_NOT_EXIST);
        }
        FileDTO fileDTO = fileService.upload(multipartFile, fileType);

        Files file = Files.builder()
                .originalFilename(fileDTO.originalFilename())
                .savedFilename(fileDTO.savedFilename())
                .fileType(fileDTO.fileType())
                .fileURI(fileDTO.fileURI())
                .build();
        fileHolder.setFiles(file);
        return filesRepository.save(file);
    }

    @Transactional
    public Files copyFile(Files files, FileType fileType) {
        FileDTO fileDTO = fileService.copy(files, fileType);

        Files copyFiles = Files.create(fileDTO);
        return filesRepository.save(copyFiles);
    }

    @Transactional
    public Files updateFile(Long fileId, MultipartFile multipartFile) {
        Files files = filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));

        if (multipartFile == null) {
            return files;
        }

        UpdateDTO updateDTO = fileService.update(files, multipartFile);
        files.updateFiles(updateDTO);
        return files;
    }

    @Transactional
    public Files updateFile(Optional<Files> optionalFiles, MultipartFile multipartFile) {
        Files files = optionalFiles.orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));
        if (multipartFile == null) {
            return files;
        }

        UpdateDTO updateDTO = fileService.update(files, multipartFile);
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

        fileService.deleteInStorage(files);
        filesRepository.delete(files);
    }

    @Transactional
    public void deleteFile(Optional<Files> optionalFiles) {
        if (optionalFiles.isEmpty()) {
            return;
        }
        Files files = optionalFiles.get();

        fileService.deleteInStorage(files);
        filesRepository.delete(files);
    }

    public Files findById(Long fileId) {
        return filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));
    }

    public FileResponse convertToFileResponse(Optional<Files> optionalFiles) {
        return optionalFiles
                .map(files -> {
                    String fileAccessURI = fileService.getFileAccessURI(files);
                    return FileResponse.createExistFile(files.getId(), fileAccessURI);
                })
                .orElseGet(FileResponse::createNotExistFile);
    }
}
