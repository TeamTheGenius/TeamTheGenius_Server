package com.genius.gitget.topic.repository;

import com.genius.gitget.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
