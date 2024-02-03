package com.genius.gitget.admin.topic.dto;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import lombok.Builder;

import java.io.IOException;
import java.util.Optional;

@Builder
public record TopicDetailResponse(Long topicId, String title, String tags,
                                  String description, String notice, int pointPerPerson, FileResponse fileResponse) {
    public static TopicDetailResponse createByEntity(Topic topic, Optional<Files> files) throws IOException {
        return TopicDetailResponse.builder()
                .topicId(topic.getId())
                .title(topic.getTitle())
                .tags(topic.getTags())
                .description(topic.getDescription())
                .notice(topic.getNotice())
                .pointPerPerson(topic.getPointPerPerson())
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
