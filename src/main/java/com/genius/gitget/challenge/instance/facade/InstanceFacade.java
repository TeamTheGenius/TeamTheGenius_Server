package com.genius.gitget.challenge.instance.facade;

import com.genius.gitget.challenge.instance.dto.home.HomeInstanceResponse;
import com.genius.gitget.challenge.instance.dto.search.InstanceSearchResponse;
import com.genius.gitget.challenge.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface InstanceFacade {
    Page<InstanceSearchResponse> searchInstances(String keyword, String progress, Pageable pageable);

    Slice<HomeInstanceResponse> getRecommendations(User user, Pageable pageable);

    Slice<HomeInstanceResponse> getInstancesByCondition(Pageable pageable);
}
