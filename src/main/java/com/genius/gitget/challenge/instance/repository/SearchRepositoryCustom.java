package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchRepositoryCustom {
    Page<InstanceSearchResponse> Search(Progress progress, String title, Pageable pageable);
}
