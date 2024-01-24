package com.genius.gitget.file.domain;

import static com.genius.gitget.util.exception.ErrorCode.NOT_SUPPORTED_IMAGE_TYPE;

import com.genius.gitget.util.exception.BusinessException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FileType {
    PROFILE("profile/"),
    TOPIC("topic/"),
    INSTANCE("instance/"),
    PET("pet/");

    private final String path;

    public static FileType fineType(String targetType) {
        return Arrays.stream(FileType.values())
                .filter(type -> type.path.contains(targetType))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_SUPPORTED_IMAGE_TYPE));
    }
}
