package com.genius.gitget.challenge.instance.util;

import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchRequest;
import com.genius.gitget.topic.dto.TopicCreateRequest;
import java.time.LocalDateTime;

public class TestDTOFactory {
    public static TopicCreateRequest createTopicCreateRequest(String title, String description, String tags,
                                                              int pointPerPerson) {
        return TopicCreateRequest.builder()
                .title(title)
                .description(description)
                .tags(tags)
                .pointPerPerson(pointPerPerson)
                .build();
    }

    public static InstanceCreateRequest createInstanceCreateRequest(Long topicId, String title, String description,
                                                                    String tags, int pointPerPerson) {
        return InstanceCreateRequest.builder()
                .topicId(topicId)
                .title(title)
                .description(description)
                .tags(tags)
                .pointPerPerson(pointPerPerson)
                .startedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now().plusDays(3))
                .build();
    }

    public static InstanceSearchRequest createInstanceSearchRequest(String keyword, String progress) {
        return InstanceSearchRequest.builder()
                .keyword(keyword)
                .progress(progress)
                .build();
    }
}
