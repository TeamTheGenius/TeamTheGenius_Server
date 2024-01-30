package com.genius.gitget.global.file.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.CREATED;
import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.instance.dto.InstanceCreateRequest;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FilesController {
    private final FilesService filesService;

    @PostMapping
    public ResponseEntity<SingleResponse<FileResponse>> uploadImage(
            @RequestPart(value = "data") InstanceCreateRequest instanceCreateRequest,
            @RequestPart(value = "files") MultipartFile multipartFile,
            @RequestPart(value = "type") String type) throws IOException {

        Files files = filesService.uploadFile(multipartFile, type);
        FileResponse fileResponse = FileResponse.createExistFile(files);

        return ResponseEntity.ok().body(
                new SingleResponse<>(CREATED.getStatus(), CREATED.getMessage(), fileResponse)
        );
    }

    @GetMapping(value = {"/{fileId}"})
    public ResponseEntity<SingleResponse<FileResponse>> getImage(@PathVariable(name = "fileId") Long fileId)
            throws IOException {

        FileResponse encodedFile = filesService.getEncodedFile(fileId);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), encodedFile)
        );
    }

    @PostMapping("/{fileId}")
    public ResponseEntity<SingleResponse<FileResponse>> updateImage(
            @RequestPart(value = "files") MultipartFile multipartFile,
            @PathVariable Long fileId
    ) throws IOException {
        Files files = filesService.updateFile(fileId, multipartFile);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(),
                        FileResponse.createExistFile(files))
        );
    }
}
