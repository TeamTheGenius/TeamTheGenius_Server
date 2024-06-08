package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.challenge.instance.domain.Progress.PREACTIVITY;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.home.HomeInstanceResponse;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class InstanceHomeService {
    private final FilesService filesService;
    private final InstanceRepository instanceRepository;

    public Slice<HomeInstanceResponse> getRecommendations(User user, Pageable pageable) {
        List<Instance> instances = new ArrayList<>();
        List<String> userTags = Arrays.stream(user.getTags().split(",")).toList();
        for (String userTag : userTags) {
            instances.addAll(instanceRepository.findRecommendations(userTag, PREACTIVITY));
        }

        List<HomeInstanceResponse> recommendations = instances.stream()
                .distinct()
                .map(this::mapToHomeInstanceResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recommendations.size());
        return new PageImpl<>(recommendations.subList(start, end), pageable, recommendations.size());
    }

    public Slice<HomeInstanceResponse> getInstancesByCondition(Pageable pageable) {

        Slice<Instance> instances = instanceRepository.findPagesByProgress(PREACTIVITY, pageable);
        return instances.map(this::mapToHomeInstanceResponse);
    }

    private HomeInstanceResponse mapToHomeInstanceResponse(Instance instance) {
        FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
        return HomeInstanceResponse.createByEntity(instance, fileResponse);
    }
}
