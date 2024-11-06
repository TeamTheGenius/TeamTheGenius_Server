package com.genius.gitget.global.file.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FileService;
import com.genius.gitget.global.file.service.FilesManager;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileManager의 기능을 별도로 테스트하기 위해 생성한 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file/test")
public class FileTestController {
    private final FilesManager filesManager;
    private final FileService fileService;


    @GetMapping("/{fileId}")
    public ResponseEntity<SingleResponse<FileResponse>> download(
            @PathVariable Long fileId
    ) {
        Files files = filesManager.findById(fileId);
        FileResponse fileResponse = filesManager.convertToFileResponse(Optional.ofNullable(files));

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), fileResponse)
        );
    }

    @PostMapping
    public ResponseEntity<SingleResponse<FileResponse>> upload(
            @RequestParam("files") MultipartFile multipartFile,
            @RequestParam("type") String type
    ) {
        FileType fileType = FileType.findType(type);
        Files files = filesManager.uploadFile(multipartFile, fileType);
        String accessURI = fileService.getFileAccessURI(files);
        FileResponse fileResponse = FileResponse.createExistFile(files.getId(), accessURI);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), fileResponse)
        );
    }

    @PatchMapping("/{fileId}")
    public ResponseEntity<SingleResponse<FileResponse>> update(
            @PathVariable Long fileId,
            @RequestParam("files") MultipartFile multipartFile) {
        Files files = filesManager.updateFile(fileId, multipartFile);
        String accessURI = fileService.getFileAccessURI(files);
        FileResponse fileResponse = FileResponse.createExistFile(files.getId(), accessURI);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), fileResponse)
        );
    }

    @PostMapping("/{fileId}")
    public ResponseEntity<SingleResponse<FileResponse>> copy(
            @PathVariable Long fileId,
            @RequestParam("type") String type) {

        FileType fileType = FileType.findType(type);
        Files files = filesManager.findById(fileId);
        Files copiedFile = filesManager.copyFile(files, fileType);

        String accessURI = fileService.getFileAccessURI(copiedFile);
        FileResponse fileResponse = FileResponse.createExistFile(copiedFile.getId(), accessURI);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), fileResponse)
        );
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<CommonResponse> delete(
            @PathVariable Long fileId
    ) {
        filesManager.deleteFile(fileId);

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }
}
