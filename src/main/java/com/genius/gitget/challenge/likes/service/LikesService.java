package com.genius.gitget.challenge.likes.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.dto.LikesDTO;
import com.genius.gitget.challenge.likes.dto.UserLikesResponse;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<UserLikesResponse> getLikesList(User user, Pageable pageable) {
        User verifiedUser = verifyUser(user);
        List<Likes> likes = verifiedUser.getLikesList();
        List<Long> likesList = new ArrayList<>();

        for (Likes like : likes) {
            Instance findLikedInstance = instanceRepository.findById(like.getInstance().getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INSTANCE_NOT_FOUND));
            likesList.add(findLikedInstance.getId());
        }
        Page<LikesDTO> likesDTOS = instanceRepository.findLikes(likesList, pageable);
        return likesDTOS.map(this::mapToUserLikesResponse);
    }

    private UserLikesResponse mapToUserLikesResponse(LikesDTO likesDTO) {
        try {
            return UserLikesResponse.createByEntity(likesDTO);
        } catch (Exception e) {
            throw new BusinessException();
        }
    }

    @Transactional
    public void addLikes(User user, String identifier, Long instanceId) {
        User verifiedUser = verifyUser(user, identifier);
        User findUser = getUser(verifiedUser.getIdentifier());
        Instance findInstance = getInstance(instanceId);

        Likes likes = new Likes(findUser, findInstance);
        likesRepository.save(likes);
    }

    @Transactional
    public void deleteLikes(User user, Long likesId) {
        Likes findLikes = getLikes(likesId);
        User findUser = getUser(findLikes.getUser().getIdentifier());
        verifyUser(user, findUser.getIdentifier());
        getInstance(findLikes.getInstance().getId());

        likesRepository.deleteById(findLikes.getId());
    }

    private Likes getLikes(Long likesId) {
        return likesRepository.findById(likesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LIKES_NOT_FOUND));
    }

    private User verifyUser(User user) {
        return userRepository.findByIdentifier(user.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private User verifyUser(User AuthenticatedUser, String identifier) {
        if (!(AuthenticatedUser.getIdentifier().equals(identifier))) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return AuthenticatedUser;
    }

    private Instance getInstance(Long instanceId) {
        return instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTANCE_NOT_FOUND));
    }

    private User getUser(String user) {
        return userRepository.findByIdentifier(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
