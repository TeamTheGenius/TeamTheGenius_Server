package com.genius.gitget.challenge.instance.dto.search;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuerydslDTO {
    private Long topicId;
    private Long instanceId;
    private Long instanceFileId;
    private String keyword;
    private int pointPerPerson;
    private int participantCount;
    private Long fileFileId;
    private String fileUri;
    private String originalFilename;
    private String savedFilename;
    private String fileType;

    public QuerydslDTO(Long topicId, Long instanceId, Long instanceFileId, String keyword, int pointPerPerson,
                       int participantCount, Long fileFileId, String fileUri, String originalFilename, String savedFilename, String fileType) {
        this.topicId = topicId;
        this.instanceId = instanceId;
        this.instanceFileId = instanceFileId;
        this.keyword = keyword;
        this.pointPerPerson = pointPerPerson;
        this.participantCount = participantCount;
        this.fileFileId = fileFileId;
        this.fileUri = fileUri;
        this.originalFilename = originalFilename;
        this.savedFilename = savedFilename;
        this.fileType = fileType;
    }
}
