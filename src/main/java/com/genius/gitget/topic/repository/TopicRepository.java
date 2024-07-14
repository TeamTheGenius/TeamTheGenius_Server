package com.genius.gitget.topic.repository;

import com.genius.gitget.topic.domain.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    @Query("select t from Topic t ORDER BY t.id DESC ")
    Page<Topic> findAllById(Pageable pageable);
}
