package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.repository.SearchRepository;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
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
    private final FilesService filesService;
    private final SearchRepository searchRepository;
    private final StringToEnum stringToEnum;

    private final String[] progressData = {"PREACTIVITY", "ACTIVITY", "DONE"};

    public Page<InstanceSearchResponse> searchInstances(String keyword, String progress, Pageable pageable) {

        Page<Instance> search;
        Progress convertProgress;
        boolean flag = false;

        for (String progressCond : progressData) {
            if (progressCond.equals(progress)) {
                flag = true;
            }
        }
        if (flag) {
            convertProgress = stringToEnum.convert(progress);
            search = searchRepository.search(convertProgress, keyword, pageable);
        } else {
            search = searchRepository.search(null, keyword, pageable);
        }
        return search.map(this::convertToSearchResponse);
    }

    private InstanceSearchResponse convertToSearchResponse(Instance instance) {
        FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
        return InstanceSearchResponse.builder()
                .topicId(instance.getTopic().getId())
                .instanceId(instance.getId())
                .keyword(instance.getTitle())
                .pointPerPerson(instance.getPointPerPerson())
                .participantCount(instance.getParticipantCount())
                .fileResponse(fileResponse)
                .build();
    }
}
