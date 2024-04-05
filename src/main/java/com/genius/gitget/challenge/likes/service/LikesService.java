package com.genius.gitget.challenge.likes.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.dto.UserLikesAddResponse;
import com.genius.gitget.challenge.likes.dto.UserLikesResponse;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        List<User> userList = verifyUser(user);
        List<Likes> likes = new ArrayList<>();

        for (User userObject : userList) {
            if (userObject.getIdentifier().equals(user.getIdentifier())) {
                likes = userObject.getLikesList();
            }
        }
        List<UserLikesResponse> userLikesResponses = new ArrayList<>();

        for (Likes like : likes) {
            Instance instance = like.getInstance();
            UserLikesResponse userLikesResponse = UserLikesResponse.builder()
                    .likesId(like.getId())
                    .instanceId(instance.getId())
                    .title(instance.getTitle())
                    .pointPerPerson(instance.getPointPerPerson())
                    .fileResponse(FileResponse.create(instance.getFiles()))
                    .build();

            userLikesResponses.add(userLikesResponse);
        }

        return new PageImpl<>(userLikesResponses, pageable, userLikesResponses.size());
    }

    @Transactional
    public UserLikesAddResponse addLikes(User user, String identifier, Long instanceId) {
        User comparedUser = compareToUserIdentifier(user, identifier);
        List<User> userList = verifyUser(comparedUser);
        User findUser = null;

        for (User userObject : userList) {
            System.out.println("!!!!!!!!!" + userObject.getIdentifier() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            if (userObject.getIdentifier().equals(identifier)) {
                findUser = userObject;
            }
        }
        Instance findInstance = verifyInstance(instanceId);

        Likes likes = new Likes(findUser, findInstance);
        Long id = likesRepository.save(likes).getId();
        return UserLikesAddResponse.builder()
                .likesId(id).build();
    }

    @Transactional
    public void deleteLikes(User user, Long likesId) {
        Likes findLikes = likesRepository.findById(likesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LIKES_NOT_FOUND));

        likesRepository.deleteById(findLikes.getId());
    }

//    @Transactional
//    public void deleteLikesLazy(User user, Long likesId) {
//        try {
//            likesRepository.deleteById(likesId);
//        } catch (Exception e) {
//            e.getStackTrace();
//        }
//    }


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