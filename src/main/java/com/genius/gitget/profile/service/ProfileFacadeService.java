package com.genius.gitget.profile.service;

import static com.genius.gitget.challenge.participant.domain.JoinResult.FAIL;
import static com.genius.gitget.challenge.participant.domain.JoinResult.PROCESSING;
import static com.genius.gitget.challenge.participant.domain.JoinResult.READY;
import static com.genius.gitget.challenge.participant.domain.JoinResult.SUCCESS;
import static com.genius.gitget.challenge.participant.domain.JoinStatus.YES;

import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesManager;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.profile.dto.UserChallengeResultResponse;
import com.genius.gitget.profile.dto.UserDetailsInformationResponse;
import com.genius.gitget.profile.dto.UserInformationResponse;
import com.genius.gitget.profile.dto.UserInformationUpdateRequest;
import com.genius.gitget.profile.dto.UserInterestResponse;
import com.genius.gitget.profile.dto.UserInterestUpdateRequest;
import com.genius.gitget.profile.dto.UserPointResponse;
import com.genius.gitget.store.item.service.OrdersService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProfileFacadeService implements ProfileFacade {
    private final UserService userService;
    private final OrdersService ordersService;
    private final FilesManager filesManager;

    public ProfileFacadeService(UserService userService, OrdersService ordersService,
                                FilesManager filesManager) {
        this.userService = userService;
        this.ordersService = ordersService;
        this.filesManager = filesManager;
    }

    @Override
    public UserPointResponse getUserPoint(User user) {
        Long userPoint = userService.getUserPoint(user.getId());
        return UserPointResponse.builder()
                .identifier(user.getIdentifier())
                .point(userPoint)
                .build();
    }

    @Override
    public UserInformationResponse getUserInformation(Long userId) {
        User findUser = userService.findUserById(userId);
        Long frameId = ordersService.getUsingFrameItem(userId).getId();
        FileResponse fileResponse = filesManager.convertToFileResponse(findUser.getFiles());
        return UserInformationResponse.createByEntity(findUser, frameId, fileResponse);
    }

    @Override
    public UserDetailsInformationResponse getUserDetailsInformation(User user) {
        User findUser = userService.findUserByIdentifier(user.getIdentifier());

        int participantCount = 0;
        List<Participant> participantInfoList = findUser.getParticipantList();

        for (int i = 0; i < participantInfoList.size(); i++) {
            if (participantInfoList.get(i).getJoinStatus() == YES) {
                JoinResult joinResult = participantInfoList.get(i).getJoinResult();
                participantCount = (joinResult == SUCCESS) ? participantCount + 1 : participantCount - 1;
            }
        }
        FileResponse fileResponse = filesManager.convertToFileResponse(findUser.getFiles());
        return UserDetailsInformationResponse.createByEntity(findUser, participantCount, fileResponse);
    }

    @Override
    @Transactional
    public Long updateUserInformation(User user, UserInformationUpdateRequest userInformationUpdateRequest) {
        User findUser = userService.findUserByIdentifier(user.getIdentifier());
        findUser.updateUserInformation(
                userInformationUpdateRequest.getNickname(),
                userInformationUpdateRequest.getInformation());

        return userService.save(findUser);
    }

    @Override
    @Transactional
    public void deleteUserInformation(User user, String reason) {
        User findUser = userService.findUserByIdentifier(user.getIdentifier());

        filesManager.deleteFile(findUser.getFiles());
        findUser.setFiles(null);
        findUser.deleteLikesList();

        userService.delete(findUser.getId(), user.getIdentifier(), reason);
    }

    @Override
    @Transactional
    public void updateUserTags(User user, UserInterestUpdateRequest userInterestUpdateRequest) {
        if (userInterestUpdateRequest.getTags() == null) {
            throw new BusinessException();
        }
        User findUser = userService.findUserByIdentifier(user.getIdentifier());
        String interest = String.join(",", userInterestUpdateRequest.getTags());
        findUser.updateUserTags(interest);
        userService.save(findUser);
    }

    @Override
    public UserInterestResponse getUserInterest(User user) {
        String tags = user.getTags();
        String[] tagsList = tags.split(",");
        for (int i = 0; i < tagsList.length; i++) {
            tagsList[i] = tagsList[i].trim();
        }
        List<String> interestList = new ArrayList<>(Arrays.asList(tagsList));
        return UserInterestResponse.builder()
                .tags(interestList)
                .build();
    }

    @Override
    public UserChallengeResultResponse getUserChallengeResult(User user) {
        User findUser = userService.findUserByIdentifier(user.getIdentifier());
        HashMap<JoinResult, List<Long>> participantHashMap = new HashMap<>() {
            {
                put(READY, new ArrayList<>());
                put(FAIL, new ArrayList<>());
                put(PROCESSING, new ArrayList<>());
                put(SUCCESS, new ArrayList<>());
            }
        };
        List<Participant> participantInfos = findUser.getParticipantList();
        for (Participant participant : participantInfos) {
            JoinResult joinResult = participant.getJoinResult();
            if (!participantHashMap.containsKey(joinResult)) {
                continue;
            }

            List<Long> participantIds = participantHashMap.get(joinResult);
            participantIds.add(participant.getId());
            participantHashMap.put(joinResult, participantIds);
        }

        int beforeStart = participantHashMap.get(READY).size();
        int fail = participantHashMap.get(FAIL).size();
        int success = participantHashMap.get(SUCCESS).size();
        int processing = participantHashMap.get(PROCESSING).size();

        return UserChallengeResultResponse.builder()
                .beforeStart(beforeStart)
                .fail(fail)
                .success(success)
                .processing(processing)
                .build();
    }
}
