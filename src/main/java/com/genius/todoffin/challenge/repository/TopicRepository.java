package com.genius.todoffin.challenge.repository;

import com.genius.todoffin.challenge.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
