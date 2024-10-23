package com.genius.gitget.global.file.service;

import static com.genius.gitget.global.util.exception.ErrorCode.FILE_NOT_EXIST;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.CopyDTO;
import com.genius.gitget.global.file.dto.FileDTO;
import com.genius.gitget.global.file.dto.UpdateDTO;
import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import java.net.URL;
import org.springframework.web.multipart.MultipartFile;

public class S3FileService implements FileService {
    private final AmazonS3 amazonS3;
    private final FileUtil fileUtil;
    private final String bucket;
    private final String cloudFrontDomain;
    public S3FileService(AmazonS3 amazonS3, FileUtil fileUtil, String bucket, String cloudFrontDomain) {
        this.amazonS3 = amazonS3;
        this.fileUtil = fileUtil;
        this.bucket = bucket;
        this.cloudFrontDomain = cloudFrontDomain;
    }

    @Override
    public String getFileAccessURI(Files files) {
        URL url = amazonS3.getUrl(bucket, files.getFileURI());
        return cloudFrontDomain + url.getFile();
    }

    @Override
    public FileDTO upload(MultipartFile multipartFile, FileType fileType) {
        try {
            fileUtil.validateFile(multipartFile);
            FileDTO fileDTO = fileUtil.getFileDTO(multipartFile, fileType, "");

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            amazonS3.putObject(bucket, fileDTO.fileURI(), multipartFile.getInputStream(), objectMetadata);
            return fileDTO;
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public FileDTO copy(Files files, FileType fileType) {
        validateFileExist(files);

        CopyDTO copyDTO = fileUtil.getCopyInfo(files, fileType, "");

        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                bucket, files.getFileURI(),
                bucket, copyDTO.fileURI()
        );
        amazonS3.copyObject(copyObjectRequest);
        return FileDTO.builder()
                .fileType(fileType)
                .originalFilename(copyDTO.originalFilename())
                .savedFilename(copyDTO.savedFilename())
                .fileURI(copyDTO.fileURI())
                .build();
    }

    @Override
    public UpdateDTO update(Files files, MultipartFile multipartFile) {
        deleteInStorage(files);
        FileDTO fileDTO = upload(multipartFile, files.getFileType());

        return UpdateDTO.of(fileDTO);
    }

    @Override
    public void deleteInStorage(Files files) {
        try {
            amazonS3.deleteObject(bucket, files.getFileURI());
        } catch (SdkClientException e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public void validateFileExist(Files files) {
        if (!amazonS3.doesObjectExist(bucket, files.getFileURI())) {
            throw new BusinessException(FILE_NOT_EXIST);
        }
    }
}
