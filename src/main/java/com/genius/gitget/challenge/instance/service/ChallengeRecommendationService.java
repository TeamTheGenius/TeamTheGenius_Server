package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.challenge.instance.domain.Progress.PREACTIVITY;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
public class ChallengeRecommendationService {
    private final InstanceRepository instanceRepository;

    public List<Instance> getRecommendations(User user) {
        String[] userTags = user.getTags().split(",");
        List<Instance> instances = new ArrayList<>();
        for (String userTag : userTags) {
            instances.addAll(instanceRepository.findRecommendations(userTag, PREACTIVITY));
        }
        return instances.stream().distinct().collect(Collectors.toList());
    }

    public Slice<Instance> getInstancesByCondition(Pageable pageable) {
        return instanceRepository.findPagesByProgress(PREACTIVITY, pageable);
    }
}
