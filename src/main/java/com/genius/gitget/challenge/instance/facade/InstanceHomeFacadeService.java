package com.genius.gitget.challenge.instance.facade;


import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.home.HomeInstanceResponse;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchRequest;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.service.InstanceRecommendationService;
import com.genius.gitget.challenge.instance.service.InstanceSearchService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
@Transactional
public class InstanceHomeFacadeService implements InstanceHomeFacade {

    private final InstanceRecommendationService instanceRecommendationService;
    private final InstanceSearchService instanceSearchService;
    private final FilesService filesService;

    @Override
    public Page<InstanceSearchResponse> searchInstancesByKeywordAndProgress(InstanceSearchRequest instanceSearchRequest,
                                                                            Pageable pageable) {
        Page<Instance> searchedInstances = instanceSearchService.searchInstances(instanceSearchRequest.keyword(),
                instanceSearchRequest.progress(), pageable);

        return searchedInstances.map(this::convertToSearchResponse);
    }

    @Override
    public Slice<HomeInstanceResponse> recommendInstances(User user, Pageable pageable) {
        List<Instance> instanceList = instanceRecommendationService.getRecommendations(user);
        List<HomeInstanceResponse> recommendations = convertToHomeInstanceResponseList(instanceList);
        return createPageFromList(recommendations, pageable);
    }

    @Override
    public Slice<HomeInstanceResponse> findInstancesByCondition(Pageable pageable) {
        Slice<Instance> instancesByCondition = instanceRecommendationService.getInstancesByCondition(pageable);
        return instancesByCondition.map(this::mapToHomeInstanceResponse);
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

    private List<HomeInstanceResponse> convertToHomeInstanceResponseList(List<Instance> instances) {
        return instances.stream()
                .map(this::mapToHomeInstanceResponse)
                .collect(Collectors.toList());
    }

    private HomeInstanceResponse mapToHomeInstanceResponse(Instance instance) {
        FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
        return HomeInstanceResponse.createByEntity(instance, fileResponse);
    }

    private Slice<HomeInstanceResponse> createPageFromList(List<HomeInstanceResponse> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
