package com.genius.gitget.challenge.instance.dto.search;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.dto.FileResponse;
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
    public InstanceSearchResponse(Long topicId, Long instanceId, String keyword, int pointPerPerson, int participantCount, Optional<Files> files) throws IOException{
        this.topicId = topicId;
        this.instanceId = instanceId;
        this.keyword = keyword;
        this.pointPerPerson = pointPerPerson;
        this.participantCount = participantCount;
        this.fileResponse = convertToFileResponse(files);
    }

    public static InstanceSearchResponse createByEntity(Instance instance, Optional<Files> files) throws IOException {
        return InstanceSearchResponse.builder()
                .topicId(instance.getTopic().getId())
                .instanceId(instance.getId())
                .keyword(instance.getTitle())
                .pointPerPerson(instance.getPointPerPerson())
                .participantCount(instance.getParticipantCount())
                .files(files)
                .build();
//        return InstanceSearchResponse.builder()
//                .topicId(instance.getTopic().getId())
//                .instanceId(instance.getId())
//                .keyword(instance.getTitle())
//                .pointPerPerson(instance.getPointPerPerson())
//                .participantCount(instance.getParticipantCount())
//                .fileResponse(convertToFileResponse(files))
//                .build();
    }

    private static FileResponse convertToFileResponse(Optional<Files> files) throws IOException {
        if (files.isEmpty()) {
            return FileResponse.createNotExistFile();
        }
        return FileResponse.createExistFile(files.get());
    }
}