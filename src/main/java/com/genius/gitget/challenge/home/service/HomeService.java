package com.genius.gitget.challenge.home.service;

import static com.genius.gitget.challenge.instance.domain.Progress.PREACTIVITY;

import com.genius.gitget.challenge.home.dto.HomeInstanceResponse;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeService {
    private final InstanceRepository instanceRepository;

    public Slice<HomeInstanceResponse> getRecommendations(User user, Pageable pageable) {
        List<String> userTags = Arrays.stream(user.getTags().split(",")).toList();

        Slice<Instance> recommendations = instanceRepository.findRecommendations(userTags, PREACTIVITY,
                pageable);

        return recommendations.map(this::mapToHomeInstanceResponse);
    }

    public Slice<HomeInstanceResponse> getInstancesByCondition(Pageable pageable) {

        Slice<Instance> instances = instanceRepository.findInstanceByCondition(PREACTIVITY, pageable);
        return instances.map(this::mapToHomeInstanceResponse);
    }

    private HomeInstanceResponse mapToHomeInstanceResponse(Instance instance) {
        try {
            return HomeInstanceResponse.createByEntity(instance, instance.getFiles());
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
}
