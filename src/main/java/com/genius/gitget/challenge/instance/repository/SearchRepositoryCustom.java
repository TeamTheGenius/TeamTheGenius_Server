package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Instance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchRepositoryCustom {
    Page<Instance> basicSearch(String title, Pageable pageable);
}
