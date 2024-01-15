package com.genius.todoffin.challenge.repository;

import com.genius.todoffin.challenge.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
}
