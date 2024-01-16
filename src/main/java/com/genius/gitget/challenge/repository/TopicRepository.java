package com.genius.gitget.challenge.repository;

import com.genius.gitget.challenge.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
