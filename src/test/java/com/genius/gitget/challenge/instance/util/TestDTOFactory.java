package com.genius.gitget.challenge.instance.util;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchRequest;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.dto.TopicCreateRequest;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
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

    public static InstanceCreateRequest getInstanceCreateRequest(Topic savedTopic, Instance instance) {
        return InstanceCreateRequest.builder()
                .topicId(savedTopic.getId())
                .title(instance.getTitle())
                .tags(instance.getTags())
                .description(instance.getDescription())
                .notice(instance.getNotice())
                .pointPerPerson(instance.getPointPerPerson())
                .startedAt(instance.getStartedDate())
                .completedAt(instance.getCompletedDate())
                .build();
    }
}
