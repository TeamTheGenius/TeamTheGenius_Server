package com.genius.gitget.global.util.config;

import com.genius.gitget.global.file.service.FileManager;
import com.genius.gitget.global.file.service.LocalFileManager;
import com.genius.gitget.global.file.service.S3FileManager;
import com.genius.gitget.global.util.formatter.LocalDateFormatter;
import com.genius.gitget.global.util.formatter.LocalDateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final Environment env;

    @Bean
    public FileManager fileManager() {
        final String fileMode = env.getProperty("file.mode");
        final String UPLOAD_PATH = env.getProperty("file.upload.path");
        assert fileMode != null;

        if (fileMode.equals("local")) {
            return new LocalFileManager(UPLOAD_PATH);
        }
        return new S3FileManager();
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
