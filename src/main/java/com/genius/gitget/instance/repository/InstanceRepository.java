package com.genius.gitget.instance.repository;

import com.genius.gitget.instance.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
}
