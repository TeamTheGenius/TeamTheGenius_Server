package com.genius.gitget.topic.dto;

import com.genius.gitget.instance.domain.Instance;

public record TopicDTO(
        String title,
        String description,
        String tags,
        int point_per_person,
        Instance instance
) {
}
