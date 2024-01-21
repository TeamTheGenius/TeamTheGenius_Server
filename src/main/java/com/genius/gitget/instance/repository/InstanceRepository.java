package com.genius.gitget.instance.repository;

import com.genius.gitget.instance.domain.Instance;
import com.genius.gitget.topic.domain.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
    Page<Instance> findAllSortById(Pageable pageable);
}
