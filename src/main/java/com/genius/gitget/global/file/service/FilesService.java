package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_DELETED;
import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_EXIST;

import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.file.dto.UploadDTO;
import com.genius.gitget.global.file.repository.FilesRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FilesService {
    private final String UPLOAD_PATH;
    private final FilesRepository filesRepository;

    public FilesService(@Value("${file.upload.path}") String UPLOAD_PATH, FilesRepository filesRepository) {
        this.UPLOAD_PATH = UPLOAD_PATH;
        this.filesRepository = filesRepository;
    }

    @Transactional
    public Files uploadFile(MultipartFile receivedFile, String typeStr) throws IOException {
        FileUtil.validateFile(receivedFile);

        UploadDTO uploadDTO = FileUtil.getUploadInfo(receivedFile, typeStr, UPLOAD_PATH);

        saveFile(receivedFile, uploadDTO.fileURI());

        Files file = Files.builder()
                .originalFilename(uploadDTO.originalFilename())
                .savedFilename(uploadDTO.savedFilename())
                .fileType(uploadDTO.fileType())
                .fileURI(uploadDTO.fileURI())
                .build();

        return filesRepository.save(file);
    }

    private void saveFile(MultipartFile file, String fileURI) throws IOException {
        File targetFile = new File(fileURI);

        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        file.transferTo(targetFile);
    }

    @Transactional
    public Files updateFile(Long fileId, MultipartFile file) throws IOException {
        Files files = filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));

        deleteFilesInStorage(files);

        UpdateDTO updateDTO = FileUtil.getUpdateInfo(file, files.getFileType(), UPLOAD_PATH);
        saveFile(file, updateDTO.fileURI());
        files.updateFiles(updateDTO);
        return files;
    }

    /**
     * NOTE: 삭제하고자하는 Files 엔티티와 연관관계에 있는 엔티티에서 연관관계를 끊어줘야 합니다.
     *
     * @param fileId 삭제하고자하는 Files 엔티티의 PK
     */
    @Transactional
    public void deleteFile(Long fileId) throws IOException {
        Files files = filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));

        deleteFilesInStorage(files);
        filesRepository.delete(files);
    }

    private void deleteFilesInStorage(Files files) {
        String fileURI = files.getFileURI();
        File targetFile = new File(fileURI);
        if (!targetFile.delete()) {
            throw new BusinessException(FILE_NOT_DELETED);
        }
    }

    public FileResponse getEncodedFile(Long fileId) throws IOException {
        Optional<Files> optionalFiles = filesRepository.findById(fileId);
        if (optionalFiles.isEmpty()) {
            return FileResponse.createNotExistFile();
        }

        return FileResponse.createExistFile(optionalFiles.get());
    }

    public UrlResource getFile(Long fileId) throws MalformedURLException {
        Files files = filesRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));

        return new UrlResource("file:" + files.getFileURI());
    }
}
