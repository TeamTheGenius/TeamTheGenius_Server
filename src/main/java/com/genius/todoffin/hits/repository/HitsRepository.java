package com.genius.todoffin.hits.repository;

import com.genius.todoffin.hits.domain.Hits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HitsRepository extends JpaRepository<Hits, Long> {
}
