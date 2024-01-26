package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Instance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

    @Query("select i from Instance i ORDER BY i.id DESC ")
    Page<Instance> findAllById(Pageable pageable);
}
