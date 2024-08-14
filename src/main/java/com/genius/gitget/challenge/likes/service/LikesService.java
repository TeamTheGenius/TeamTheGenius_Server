package com.genius.gitget.challenge.likes.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class LikesService {
    private final UserRepository userRepository;
    private final InstanceRepository instanceRepository;
    private final LikesRepository likesRepository;

    public List<Likes> getLikesList(User user) {
        List<User> userList = verifyUser(user);
        List<Likes> likes = new ArrayList<>();

        for (User userData : userList) {
            if (userData.getIdentifier().equals(user.getIdentifier())) {
                likes = userData.getLikesList();
            }
        }
        return likes;
    }

    @Transactional
    public Long addLikes(User user, String identifier, Long instanceId) {
        User comparedUser = compareToUserIdentifier(user, identifier);
        List<User> userList = verifyUser(comparedUser);
        User findUser = null;

        for (User userObject : userList) {
            if (userObject.getIdentifier().equals(identifier)) {
                findUser = userObject;
            }
        }
        Instance findInstance = verifyInstance(instanceId);

        Likes likes = Likes.builder()
                .instance(findInstance)
                .user(findUser)
                .build();

        return likesRepository.save(likes).getId();
    }

    @Transactional
    public void deleteLikes(Long likesId) {
        Likes findLikes = likesRepository.findById(likesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LIKES_NOT_FOUND));

        likesRepository.deleteById(findLikes.getId());
    }

    private List<User> verifyUser(User user) {
        return userRepository.findAllByIdentifier(user.getIdentifier());
    }

    private Instance verifyInstance(Long instanceId) {
        return instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTANCE_NOT_FOUND));
    }

    private User compareToUserIdentifier(User AuthenticatedUser, String identifier) {
        if (!(AuthenticatedUser.getIdentifier().equals(identifier))) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return AuthenticatedUser;
    }
}