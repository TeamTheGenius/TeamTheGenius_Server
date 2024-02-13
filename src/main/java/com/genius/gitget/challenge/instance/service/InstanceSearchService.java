package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.dto.search.QuerydslDTO;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import com.genius.gitget.global.file.domain.Files;
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
import java.util.stream.Stream;

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

        Progress convertProgress = stringToEnum.convert(progress);
        searchRepository.Search(convertProgress, keyword, pageable);


        //return search.map(this::convertToInstanceSearchResponse);
        return null;
    }

//    private InstanceSearchResponse convertToInstanceSearchResponse(Instance instance) {
//        try {
//            return InstanceSearchResponse.createByEntity(instance, instance.getFiles());
//        } catch (IOException e) {
//            throw new BusinessException(e);
//        }
//    }
}
