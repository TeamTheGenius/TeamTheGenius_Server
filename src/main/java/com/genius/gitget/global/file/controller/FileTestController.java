package com.genius.gitget.global.file.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FileManager;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final FilesService filesService;
    private final FileManager fileManager;

    @PostMapping
    public ResponseEntity<SingleResponse<FileResponse>> upload(
            @RequestParam("files") MultipartFile multipartFile
    ) {
        Files files = filesService.uploadFile(multipartFile, FileType.TOPIC);
        String encodedImage = fileManager.getEncodedImage(files);
        FileResponse fileResponse = FileResponse.createExistFile(files.getId(), encodedImage);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), fileResponse)
        );
    }

    @PatchMapping("/{fileId}")
    public ResponseEntity<SingleResponse<FileResponse>> update(
            @PathVariable Long fileId,
            @RequestParam("files") MultipartFile multipartFile) {
        Files files = filesService.updateFile(fileId, multipartFile);
        String encodedImage = fileManager.getEncodedImage(files);
        FileResponse fileResponse = FileResponse.createExistFile(files.getId(), encodedImage);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), fileResponse)
        );
    }

    @PostMapping("/{fileId}")
    public ResponseEntity<SingleResponse<FileResponse>> copy(
            @PathVariable Long fileId,
            @RequestParam("type") String type) {

        FileType fileType = FileType.findType(type);
        Files files = filesService.findById(fileId);
        Files copiedFile = filesService.copyFile(files, fileType);

        String encodedImage = fileManager.getEncodedImage(copiedFile);
        FileResponse fileResponse = FileResponse.createExistFile(copiedFile.getId(), encodedImage);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), fileResponse)
        );
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<CommonResponse> delete(
            @PathVariable Long fileId
    ) {
        filesService.deleteFile(fileId);

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }
}
