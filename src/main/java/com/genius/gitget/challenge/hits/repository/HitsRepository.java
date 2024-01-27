package com.genius.gitget.challenge.hits.repository;

import com.genius.gitget.challenge.hits.domain.Hits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HitsRepository extends JpaRepository<Hits, Long> {
}
