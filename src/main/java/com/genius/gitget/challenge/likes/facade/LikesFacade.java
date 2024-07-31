package com.genius.gitget.challenge.likes.facade;

import com.genius.gitget.challenge.likes.dto.UserLikesAddResponse;
import com.genius.gitget.challenge.likes.dto.UserLikesResponse;
import com.genius.gitget.challenge.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikesFacade {
    Page<UserLikesResponse> getLikesList(User user, Pageable pageable);

    UserLikesAddResponse addLikes(User user, String identifier, Long instanceId);

    void deleteLikes(User user, Long likesId);
}
