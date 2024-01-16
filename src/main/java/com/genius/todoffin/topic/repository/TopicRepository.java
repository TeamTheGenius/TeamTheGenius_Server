package com.genius.todoffin.topic.repository;

import com.genius.todoffin.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
