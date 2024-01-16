package com.genius.gitget.challenge.repository;

import com.genius.gitget.challenge.domain.Hits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HitsRepository extends JpaRepository<Hits, Long> {
}
