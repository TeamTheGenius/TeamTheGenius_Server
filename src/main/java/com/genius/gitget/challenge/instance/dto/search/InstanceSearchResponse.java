package com.genius.gitget.challenge.instance.dto.search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class InstanceSearchResponse {
    private Long topicId;
    private Long instanceId;
    private String keyword;
    private int pointPerPerson;
    private int participantCount;

    @Builder
    @QueryProjection
    public InstanceSearchResponse(Long topicId, Long instanceId, String keyword, int pointPerPerson,
                                  int participantCount) {
        this.topicId = topicId;
        this.instanceId = instanceId;
        this.keyword = keyword;
        this.pointPerPerson = pointPerPerson;
        this.participantCount = participantCount;
    }
}