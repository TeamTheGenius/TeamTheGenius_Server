package com.genius.gitget.challenge.home.service;

import com.genius.gitget.challenge.home.dto.RecommendPagingResponse;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.domain.User;
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


    public Slice<RecommendPagingResponse> getRecommendations(User user, Pageable pageable) {
        List<String> userTags = Arrays.stream(user.getTags().split(",")).toList();

        Slice<Instance> suggestions = instanceRepository.findRecommendations(userTags, Progress.ACTIVITY, pageable);
        return suggestions.map(RecommendPagingResponse::createByEntity);
    }
}
