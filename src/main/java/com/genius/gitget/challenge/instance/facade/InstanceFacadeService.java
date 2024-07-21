package com.genius.gitget.challenge.instance.facade;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.home.HomeInstanceResponse;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.instance.service.ChallengeRecommendationService;
import com.genius.gitget.challenge.instance.service.InstanceSearchService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
@Transactional(readOnly = true)
public class InstanceFacadeService implements InstanceFacade {
    private final FilesService filesService;
    private final InstanceSearchService instanceSearchService;
    private final ChallengeRecommendationService challengeRecommendationService;


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

    @Override
    public Page<InstanceSearchResponse> searchInstances(String keyword, String progress, Pageable pageable) {
        Page<Instance> instances = instanceSearchService.searchInstances(keyword, progress, pageable);
        return instances.map(this::convertToSearchResponse);
    }

    @Override
    public Slice<HomeInstanceResponse> getRecommendations(User user, Pageable pageable) {
        return challengeRecommendationService.getRecommendations(user, pageable);
    }

    @Override
    public Slice<HomeInstanceResponse> getInstancesByCondition(Pageable pageable) {
        return challengeRecommendationService.getInstancesByCondition(pageable);
    }

}
