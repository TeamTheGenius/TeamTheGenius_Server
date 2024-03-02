package com.genius.gitget.challenge.hits.service;

import com.genius.gitget.challenge.hits.domain.Likes;
import com.genius.gitget.challenge.hits.dto.UserLikesResponse;
import com.genius.gitget.challenge.hits.repository.LikesRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LikesService {
    private final UserRepository userRepository;
    private final InstanceRepository instanceRepository;
    private final LikesRepository likesRepository;

    public Slice<UserLikesResponse> getLikesList(User user, Pageable pageable) {
        User verifiedUser = verifyUser(user);
        List<Likes> likesList = verifiedUser.getLikesList();
        ArrayList<Long> instanceArrayList = new ArrayList<>();
        for (Likes likes : likesList) {
            instanceArrayList.add(likes.getInstance().getId());
        }
        findInstanceList(instanceArrayList);

        //return likes.map(this::mapToUserLikesResponse);
        return null;
    }

    private void findInstanceList(ArrayList<Long> instanceArrayList) {

    }

    private UserLikesResponse mapToUserLikesResponse(Likes likes) {
        try {
            //return UserLikesResponse.createByEntity(likes);
            return null;
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
        Instance findInstance = getInstance(findLikes.getInstance().getId());
        User verifiedUser = verifyUser(user, findUser.getIdentifier());

        findLikes.deleteUserAndLikes(verifiedUser, findInstance);
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
