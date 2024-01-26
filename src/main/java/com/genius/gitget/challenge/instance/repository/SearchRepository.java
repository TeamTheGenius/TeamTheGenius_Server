package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Instance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SearchRepository extends JpaRepository<Instance, Long> {

    // 검색 키워드인 제목을 포함하는 경우
    Page<Instance> findByTitleContainingOrderByStartedDateDesc(String title, Pageable pageable);
}
