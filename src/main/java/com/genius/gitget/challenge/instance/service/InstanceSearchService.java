package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    public Page<Instance> searchInstances(String keyword, String progress, Pageable pageable) {
        boolean isValidProgress = false;

        List<String> progressData = Arrays.stream(Progress.values()).map(Objects::toString).toList();

        for (String progressCond : progressData) {
            if (progressCond.equals(progress)) {
                isValidProgress = true;
                break;
            }
        }
        if (isValidProgress) {
            Progress convertProgress = stringToEnum.convert(progress);
            return searchRepository.search(convertProgress, keyword, pageable);
        }
        return searchRepository.search(null, keyword, pageable);
    }
}