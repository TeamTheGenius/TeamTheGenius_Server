package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.challenge.instance.domain.Progress.PREACTIVITY;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.home.HomeInstanceResponse;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChallengeRecommendationService {
    private final FilesService filesService;
    private final InstanceRepository instanceRepository;

    public Slice<HomeInstanceResponse> getRecommendations(User user, Pageable pageable) {
        List<Instance> instances = getInstancesByUserTags(user.getTags().split(","));
        List<HomeInstanceResponse> recommendations = convertToHomeInstanceResponseList(instances);

        return createPageFromList(recommendations, pageable);
    }

    public Slice<HomeInstanceResponse> getInstancesByCondition(Pageable pageable) {
        Slice<Instance> instances = instanceRepository.findPagesByProgress(PREACTIVITY, pageable);
        return instances.map(this::mapToHomeInstanceResponse);
    }

    private List<Instance> getInstancesByUserTags(String[] userTags) {
        List<Instance> instances = new ArrayList<>();
        for (String userTag : userTags) {
            instances.addAll(instanceRepository.findRecommendations(userTag, PREACTIVITY));
        }
        return instances.stream().distinct().collect(Collectors.toList());
    }

    private List<HomeInstanceResponse> convertToHomeInstanceResponseList(List<Instance> instances) {
        return instances.stream()
                .map(this::mapToHomeInstanceResponse)
                .collect(Collectors.toList());
    }

    private Slice<HomeInstanceResponse> createPageFromList(List<HomeInstanceResponse> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    private HomeInstanceResponse mapToHomeInstanceResponse(Instance instance) {
        FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
        return HomeInstanceResponse.createByEntity(instance, fileResponse);
    }
}
