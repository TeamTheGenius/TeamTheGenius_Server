package com.genius.gitget.profile.service;

import static com.genius.gitget.challenge.participant.domain.JoinResult.FAIL;
import static com.genius.gitget.challenge.participant.domain.JoinResult.PROCESSING;
import static com.genius.gitget.challenge.participant.domain.JoinResult.READY;
import static com.genius.gitget.challenge.participant.domain.JoinResult.SUCCESS;
import static com.genius.gitget.challenge.participant.domain.JoinStatus.YES;

import com.genius.gitget.admin.signout.Signout;
import com.genius.gitget.admin.signout.SignoutRepository;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.profile.dto.UserChallengeResultResponse;
import com.genius.gitget.profile.dto.UserDetailsInformationResponse;
import com.genius.gitget.profile.dto.UserInformationResponse;
import com.genius.gitget.profile.dto.UserInformationUpdateRequest;
import com.genius.gitget.profile.dto.UserInterestResponse;
import com.genius.gitget.profile.dto.UserInterestUpdateRequest;
import com.genius.gitget.profile.dto.UserPointResponse;
import com.genius.gitget.store.item.service.OrdersProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileService {
    private final UserRepository userRepository;
    private final FilesService filesService;
    private final SignoutRepository signoutRepository;
    private final OrdersProvider ordersProvider;

    // 포인트 조회
    public UserPointResponse getUserPoint(User user) {
        return UserPointResponse.builder()
                .identifier(user.getIdentifier())
                .point(user.getPoint())
                .build();
    }

    // 사용자 정보 조회
    public UserInformationResponse getUserInformation(Long userId) {
        User findUser = getUserById(userId);
        Long frameId = ordersProvider.getUsingFrameItem(userId).getId();

        FileResponse fileResponse = filesService.convertToFileResponse(findUser.getFiles());
        return UserInformationResponse.createByEntity(findUser, frameId, fileResponse);
    }

    // 마이페이지 - 사용자 정보 상세 조회
    public UserDetailsInformationResponse getUserDetailsInformation(User user) {
        User findUser = getUserByIdentifier(user.getIdentifier());
        int participantCount = 0;
        List<Participant> participantInfoList = findUser.getParticipantList();

        for (int i = 0; i < participantInfoList.size(); i++) {
            if (participantInfoList.get(i).getJoinStatus() == YES) {
                JoinResult joinResult = participantInfoList.get(i).getJoinResult();
                participantCount = (joinResult == SUCCESS) ? participantCount + 1 : participantCount - 1;
            }
        }
        FileResponse fileResponse = filesService.convertToFileResponse(findUser.getFiles());
        return UserDetailsInformationResponse.createByEntity(findUser, participantCount, fileResponse);
    }

    // 마이페이지 - 사용자 정보 수정
    @Transactional
    public Long updateUserInformation(User user, UserInformationUpdateRequest userInformationUpdateRequest) {
        User findUser = getUserByIdentifier(user.getIdentifier());
        findUser.updateUserInformation(
                userInformationUpdateRequest.getNickname(),
                userInformationUpdateRequest.getInformation());

        User updatedUser = userRepository.save(findUser);
        return updatedUser.getId();
    }

    // 마이페이지 - 회원 탈퇴
    @Transactional
    public void deleteUserInformation(User user, String reason) {
        User findUser = getUserByIdentifier(user.getIdentifier());

        filesService.deleteFile(findUser.getFiles());
        findUser.setFiles(null);

        findUser.deleteLikesList();
        userRepository.deleteById(findUser.getId());
        signoutRepository.save(
                Signout.builder()
                        .identifier(user.getIdentifier())
                        .reason(reason)
                        .build()
        );
    }

    // 마이페이지 - 관심사 수정
    @Transactional
    public void updateUserTags(User user, UserInterestUpdateRequest userInterestUpdateRequest) {
        if (userInterestUpdateRequest.getTags() == null) {
            throw new BusinessException();
        }
        User findUser = getUserByIdentifier(user.getIdentifier());
        String interest = String.join(",", userInterestUpdateRequest.getTags());
        findUser.updateUserTags(interest);
        userRepository.save(findUser);
    }

    // 관심사 조회
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

    // 마이페이지 - 챌린지 현황
    public UserChallengeResultResponse getUserChallengeResult(User user) {
        User findUser = getUserByIdentifier(user.getIdentifier());
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

    private User getUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
