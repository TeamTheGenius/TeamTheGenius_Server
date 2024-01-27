package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Instance;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

    @Query("select i from Instance i ORDER BY i.id DESC ")
    Page<Instance> findAllById(Pageable pageable);

    @Query("select i from Instance i where i.tags in :targetTags")
    Slice<Instance> findSuggestions(@Param("targetTags") List<String> targetTags, Pageable pageable);
}
