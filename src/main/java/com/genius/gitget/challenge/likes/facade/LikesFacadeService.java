package com.genius.gitget.challenge.likes.facade;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.dto.UserLikesAddResponse;
import com.genius.gitget.challenge.likes.dto.UserLikesResponse;
import com.genius.gitget.challenge.likes.service.LikesService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesManager;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class LikesFacadeService implements LikesFacade {

    LikesService likesService;
    FilesManager filesManager;

    public LikesFacadeService(LikesService likesService, FilesManager filesManager) {
        this.likesService = likesService;
        this.filesManager = filesManager;
    }

    @Override
    public Page<UserLikesResponse> getLikesList(User user, Pageable pageable) {
        LinkedList<UserLikesResponse> userLikesResponses = new LinkedList<>();

        List<Likes> likesList = likesService.getLikesList(user);

        for (Likes like : likesList) {
            Instance instance = like.getInstance();
            FileResponse fileResponse = filesManager.convertToFileResponse(instance.getFiles());
            UserLikesResponse userLikesResponse = getUserLikesResponse(like, instance, fileResponse);
            userLikesResponses.addFirst(userLikesResponse);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userLikesResponses.size());
        return new PageImpl<>(userLikesResponses.stream().toList().subList(start, end), pageable,
                userLikesResponses.size());
    }

    @Override
    public UserLikesAddResponse addLikes(User user, String identifier, Long instanceId) {
        Long id = likesService.addLikes(user, identifier, instanceId);
        return UserLikesAddResponse.builder()
                .likesId(id).build();
    }

    @Override
    public void deleteLikes(User user, Long likesId) {
        likesService.deleteLikes(likesId);
    }

    private UserLikesResponse getUserLikesResponse(Likes like, Instance instance, FileResponse fileResponse) {
        return UserLikesResponse.builder()
                .likesId(like.getId())
                .instanceId(instance.getId())
                .title(instance.getTitle())
                .pointPerPerson(instance.getPointPerPerson())
                .fileResponse(fileResponse)
                .build();
    }
}
