package com.genius.gitget.global.util.config;

import com.genius.gitget.global.file.service.FileService;
import com.genius.gitget.global.file.service.FileUtil;
import com.genius.gitget.global.file.service.LocalFileService;
import com.genius.gitget.global.file.service.S3FileService;
import com.genius.gitget.global.util.formatter.LocalDateFormatter;
import com.genius.gitget.global.util.formatter.LocalDateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final S3Config s3Config;
    private final Environment env;


    @Bean
    public FileUtil fileUtil() {
        return new FileUtil();
    }

    @Bean
    public FileService fileManager() {
        final String fileMode = env.getProperty("file.mode");
        assert fileMode != null;

        if (fileMode.equals("local")) {
            final String UPLOAD_PATH = env.getProperty("file.upload.path");
            return new LocalFileService(fileUtil(), UPLOAD_PATH);
        }

        final String bucket = env.getProperty("cloud.aws.s3.bucket");
        return new S3FileService(s3Config.amazonS3Client(), fileUtil(), bucket);
    }

    @Bean
    public AesBytesEncryptor aesBytesEncryptor() {
        return new AesBytesEncryptor(
                env.getProperty("github.encryptSecretKey"),
                env.getProperty("github.salt"));
    }

    @Bean
    public LocalDateFormatter localDateFormatter() {
        return new LocalDateFormatter();
    }

    @Bean
    public LocalDateTimeFormatter localDateTimeFormatter() {
        return new LocalDateTimeFormatter();
    }
}
