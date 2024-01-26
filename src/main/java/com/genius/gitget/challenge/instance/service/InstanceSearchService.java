package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.dto.search.SearchDTO;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstanceSearchService {

    private final SearchRepository searchRepository;

    public Page<InstanceSearchResponse> searchInstances(SearchDTO searchDTO, Pageable pageable) {
        Page<Instance> finByTitleContaining = searchRepository.findByTitleContainingOrderByStartedDateDesc(searchDTO.keyword(), pageable);
        return finByTitleContaining.map(this::convertToInstanceSearchResponse);
    }

    private InstanceSearchResponse convertToInstanceSearchResponse(Instance instance) {
        return new InstanceSearchResponse(instance.getTopic().getId(), instance.getId(), instance.getTitle(), instance.getPointPerPerson(), instance.getParticipantInfoList().size());
    }
}
