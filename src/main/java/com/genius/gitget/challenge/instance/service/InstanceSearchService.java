package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.domain.StringToEnum;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchRequest;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InstanceSearchService {

    private final SearchRepository searchRepository;
    private final StringToEnum stringToEnum;

    public Page<InstanceSearchResponse> searchInstances(String keyword, String progress, Pageable pageable) {
        Page<Instance> finByTitleContaining;

        if (stringToEnum.convert(progress) == Progress.ALL) {
            finByTitleContaining = searchRepository.findByTitleContainingOrderByStartedDateDesc(keyword, pageable);
        } else {
            Progress convertProgress = stringToEnum.convert(progress); // Progress convertProgress = Progress.from(progress);
            finByTitleContaining = searchRepository.findByProgressAndTitleContainingOrderByStartedDateDesc(convertProgress, keyword, pageable);

        }
        return finByTitleContaining.map(this::convertToInstanceSearchResponse);
    }

    private InstanceSearchResponse convertToInstanceSearchResponse(Instance instance) {
        return new InstanceSearchResponse(instance.getTopic().getId(), instance.getId(), instance.getTitle(), instance.getPointPerPerson(), instance.getParticipantInfoList().size());
    }
}
