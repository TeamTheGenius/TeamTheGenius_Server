package com.genius.gitget.challenge.likes.dto;

import com.genius.gitget.global.file.domain.Files;

public interface LikesDTO {
    Long getInstanceId();

    String getTitle();

    Integer getPointPerPerson();

    Files getFiles();
}
