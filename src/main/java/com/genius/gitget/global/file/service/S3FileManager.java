package com.genius.gitget.global.file.service;

import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.CopyDTO;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.file.dto.UploadDTO;
import org.springframework.web.multipart.MultipartFile;

public class S3FileManager implements FileManager {
    @Override
    public String getEncodedImage(Files files) {
        return null;
    }

    @Override
    public UploadDTO upload(MultipartFile multipartFile, FileType fileType) {
        return null;
    }

    @Override
    public CopyDTO copy(Files files, FileType fileType) {
        return null;
    }

    @Override
    public UpdateDTO update(Files files, MultipartFile multipartFile) {
        return null;
    }

    @Override
    public void deleteInStorage(Files files) {

    }
}
