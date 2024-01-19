package com.genius.gitget.hits.repository;

import com.genius.gitget.hits.domain.Hits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HitsRepository extends JpaRepository<Hits, Long> {
}
