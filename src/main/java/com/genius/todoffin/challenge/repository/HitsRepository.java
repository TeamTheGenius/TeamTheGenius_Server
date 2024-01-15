package com.genius.todoffin.challenge.repository;

import com.genius.todoffin.challenge.domain.Hits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HitsRepository extends JpaRepository<Hits, Long> {
}
