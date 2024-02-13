package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    private final String[] progressData = {"PREACTIVITY", "ACTIVITY", "DONE"};

    public Page<InstanceSearchResponse> searchInstances(String keyword, String progress, Pageable pageable){
        Progress convertProgress;
        Boolean flag = Boolean.FALSE;

        for (String progressCond : progressData) {
            if (progressCond.equals(progress)) {
                flag = Boolean.TRUE;
            }
        }
        if (flag) {
            convertProgress = stringToEnum.convert(progress);
            return searchRepository.Search(convertProgress, keyword, pageable);
        } else {
            return searchRepository.Search(null, keyword, pageable);
        }
    }
}
