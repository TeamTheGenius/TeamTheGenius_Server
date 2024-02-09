package com.genius.gitget.challenge.instance.dto.search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuerydslDTO {
    private Long topicId;
    private Long instanceId;
    private Long fileId;
    private String keyword;
    private int pointPerPerson;
    private int participantCount;

    @QueryProjection
    public QuerydslDTO(Long topicId, Long instanceId, Long fileId, String keyword, int pointPerPerson, int participantCount) {
        this.topicId = topicId;
        this.instanceId = instanceId;
        this.fileId = fileId;
        this.keyword = keyword;
        this.pointPerPerson = pointPerPerson;
        this.participantCount = participantCount;
    }
}
