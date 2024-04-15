package com.genius.gitget.global.file.dto;

import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.service.FileUtil;
import java.util.Optional;

public record FileResponse(
        Long fileId,
        String encodedFile) {

    public static FileResponse create(Optional<Files> optionalFiles) {
        if (optionalFiles.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(optionalFiles.get());
    }

    public static FileResponse createExistFile(Files files) {
        //TODO: FileUtil.encodedImage()의 내용이 로컬 저장소 구현에 종속되어 있음. 수정 필요
        return new FileResponse(files.getId(), FileUtil.encodedImage(files));
    }

    public static FileResponse createNotExistFile() {
        return new FileResponse(0L, "none");
    }
}
