package com.genius.gitget.global.file.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class FileEnv {
    private static Environment environment;

    @Autowired
    public FileEnv(Environment env) {
        environment = env;
    }

    public static String getFileEnvironment() {
        return environment.getProperty("file.mode").toUpperCase();
    }
}