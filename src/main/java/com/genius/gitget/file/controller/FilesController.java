package com.genius.gitget.file.controller;

import static com.genius.gitget.util.exception.SuccessCode.CREATED;

import com.genius.gitget.file.dto.FileResponse;
import com.genius.gitget.file.service.FilesService;
import com.genius.gitget.util.response.dto.SingleResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
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
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "type") String type) throws IOException {

        FileResponse fileResponse = filesService.uploadFile(image, type);

        return ResponseEntity.ok().body(
                new SingleResponse<>(CREATED.getStatus(), CREATED.getMessage(), fileResponse)
        );
    }

    @GetMapping(value = {"/{fileId}"},
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> downloadImage(@PathVariable(name = "fileId") Long fileId)
            throws MalformedURLException {

        Resource urlResource = filesService.getFile(fileId);
        return ResponseEntity.ok(urlResource);
    }
}