package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SearchRepository extends JpaRepository<Instance, Long>, SearchRepositoryCustom {
}
