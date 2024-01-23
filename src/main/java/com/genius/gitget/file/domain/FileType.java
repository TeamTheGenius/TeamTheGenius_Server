package com.genius.gitget.file.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FileType {
    PROFILE("profile"),
    TOPIC("topic"),
    INSTANCE("instance"),
    PET("pet");

    private final String path;
}
