package com.genius.todoffin.instance.repository;

import com.genius.todoffin.instance.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
}
