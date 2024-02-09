package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InstanceSearchService {

    private final SearchRepository searchRepository;
    private final InstanceRepository instanceRepository;
    private final StringToEnum stringToEnum;
    private final FilesService filesService;

    public Page<InstanceSearchResponse> searchInstances(String keyword, String progress, Pageable pageable){

        // TODO 검색기능에 이미지 파일

        Page<Instance> findByTitleContaining;

        if (stringToEnum.convert(progress) == Progress.ALL) {
            findByTitleContaining = searchRepository.findByTitleContainingOrderByStartedDateDesc(keyword, pageable);
        } else {
            Progress convertProgress = stringToEnum.convert(progress); // Progress convertProgress = Progress.from(progress);
            findByTitleContaining = searchRepository.findByProgressAndTitleContainingOrderByStartedDateDesc(convertProgress, keyword, pageable);
        }

        return findByTitleContaining.map(this::convertToInstanceSearchResponse);
    }

    private InstanceSearchResponse convertToInstanceSearchResponse(Instance instance) {
        try {
            return InstanceSearchResponse.createByEntity(instance, instance.getFiles());
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
}
