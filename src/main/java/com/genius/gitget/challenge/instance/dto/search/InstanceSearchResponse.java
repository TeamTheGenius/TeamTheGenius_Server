package com.genius.gitget.challenge.instance.dto.search;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FileUtil;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.IOException;
import java.util.Optional;

@NoArgsConstructor
@Data
public class InstanceSearchResponse {
    private Long topicId;
    private Long instanceId;
    private String keyword;
    private int pointPerPerson;
    private int participantCount;
    private FileResponse fileResponse;

    @Builder
    @QueryProjection
    public InstanceSearchResponse(Long topicId, Long instanceId, String keyword, int pointPerPerson, int participantCount, Files files) throws IOException {
        this.topicId = topicId;
        this.instanceId = instanceId;
        this.keyword = keyword;
        this.pointPerPerson = pointPerPerson;
        this.participantCount = participantCount;
        this.fileResponse = convertToFileResponse(Optional.of(files));
    }


    private static FileResponse convertToFileResponse(Optional<Files> files) throws IOException {
        if (files.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(files.get());
    }
}