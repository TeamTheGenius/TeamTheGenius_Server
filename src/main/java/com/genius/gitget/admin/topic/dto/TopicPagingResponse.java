package com.genius.gitget.admin.topic.dto;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;

import java.io.IOException;
import java.util.Optional;

@Builder
public record TopicPagingResponse(Long topicId, String title, FileResponse fileResponse) {

    public static TopicPagingResponse createByEntity(Topic topic, Optional<Files> files) throws IOException {
        return TopicPagingResponse.builder()
                .topicId(topic.getId())
                .title(topic.getTitle())
                .fileResponse(convertToFileResponse(files))
                .build();
    }

    private static FileResponse convertToFileResponse(Optional<Files> files) throws IOException {
        if (files.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(files.get());
    }
}
