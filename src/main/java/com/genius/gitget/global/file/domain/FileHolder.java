package com.genius.gitget.global.file.domain;

import java.util.Optional;

public interface FileHolder {
    Optional<Files> getFiles();

    void setFiles(Files files);
}
