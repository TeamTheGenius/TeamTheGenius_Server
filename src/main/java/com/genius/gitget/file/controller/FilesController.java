package com.genius.gitget.file.controller;

import static com.genius.gitget.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.file.service.FilesService;
import com.genius.gitget.util.response.dto.SingleResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SingleResponse<Long>> uploadImage(
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "type") String type) throws IOException {

        Long savedFileId = filesService.uploadFile(image, type);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), savedFileId)
        );
    }
}
