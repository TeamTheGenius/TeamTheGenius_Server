package com.genius.gitget.global.file.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.global.file.domain.FileHolder;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FileHolderFinder;
import com.genius.gitget.global.file.service.FilesManager;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FilesController {
    private final FileHolderFinder finder;
    private final FilesManager filesManager;


    @PostMapping("/{id}")
    public ResponseEntity<SingleResponse<FileResponse>> uploadFile(
            @PathVariable Long id,
            @RequestParam("type") String type,
            @RequestParam(value = "files", required = false) MultipartFile multipartFile
    ) {
        FileType fileType = FileType.findType(type);
        FileHolder fileHolder = finder.findByInfo(id, fileType);
        Files files;

        files = filesManager.uploadFile(fileHolder, multipartFile, fileType);
        FileResponse fileResponse = filesManager.convertToFileResponse(Optional.ofNullable(files));

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), fileResponse)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SingleResponse<FileResponse>> updateFile(
            @PathVariable Long id,
            @RequestParam("type") String type,
            @RequestParam("files") MultipartFile multipartFile
    ) {
        FileType fileType = FileType.findType(type);
        FileHolder fileHolder = finder.findByInfo(id, fileType);
        Files files = filesManager.updateFile(fileHolder.getFiles(), multipartFile);
        FileResponse fileResponse = filesManager.convertToFileResponse(Optional.ofNullable(files));

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), fileResponse)
        );
    }
}