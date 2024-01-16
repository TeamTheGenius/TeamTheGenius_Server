package com.genius.gitget.challenge.repository;

import com.genius.gitget.challenge.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
}
