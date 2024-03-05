package com.genius.gitget.profile.service;

import static com.genius.gitget.challenge.participantinfo.domain.JoinResult.FAIL;
import static com.genius.gitget.challenge.participantinfo.domain.JoinResult.PROCESSING;
import static com.genius.gitget.challenge.participantinfo.domain.JoinResult.SUCCESS;
import static com.genius.gitget.challenge.participantinfo.domain.JoinStatus.YES;

import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.repository.FilesRepository;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.profile.dto.UserChallengeResultResponse;
import com.genius.gitget.profile.dto.UserInformationResponse;
import com.genius.gitget.profile.dto.UserInformationUpdateRequest;
import com.genius.gitget.profile.dto.UserTagsUpdateRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileService {
    private final UserRepository userRepository;
    private final FilesRepository filesRepository;
    private final FilesService filesService;

    // TODO 결제 내역 조회

    // 마이페이지 - 사용자 정보 조회
    public UserInformationResponse getUserInformation(User user) {
        User findUser = findUser(user.getIdentifier());
        int participantCount = 0;
        List<ParticipantInfo> participantInfoList = findUser.getParticipantInfoList();

        for (int i = 0; i < participantInfoList.size(); i++) {
            if (participantInfoList.get(i).getJoinStatus() == YES) {
                JoinResult joinResult = participantInfoList.get(i).getJoinResult();
                participantCount = (joinResult == SUCCESS) ? participantCount + 1 : participantCount - 1;
            }
        }

        try {
            Files files = filesRepository.findById(findUser.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_EXIST));
            return UserInformationResponse.entityToDto(findUser, files, participantCount);
        } catch (Exception e) {
            return UserInformationResponse.entityToDto(findUser, null, participantCount);
        }
    }

    // 마이페이지 - 사용자 정보 수정
    @Transactional
    public void updateUserInformation(User user, UserInformationUpdateRequest userInformationUpdateRequest,
                                      MultipartFile multipartFile, String type) {
        User findUser = findUser(user.getIdentifier());
        findUser.updateUserInformation(
                userInformationUpdateRequest.getNickname(),
                userInformationUpdateRequest.getInformation());

        if (multipartFile != null) {
            if (findUser.getFiles() == null) {
                Files uploadedFile = filesService.uploadFile(multipartFile, type);
                findUser.setFiles(uploadedFile);
            } else {
                filesService.updateFile(findUser.getFiles().getId(), multipartFile);
            }
        }
        userRepository.save(findUser);
    }

    // 마이페이지 - 회원 탈퇴
    @Transactional
    public void deleteUserInformation(User user) {
        User findUser = findUser(user.getIdentifier());
        findUser.setFiles(null);
        findUser.deleteLikesList();
        userRepository.deleteById(findUser.getId());
    }

    // 마이페이지 - 관심사 수정
    @Transactional
    public void updateUserTags(User user, UserTagsUpdateRequest userTagsUpdateRequest) {
        if (userTagsUpdateRequest.getTags() == null) {
            throw new BusinessException();
        }
        User findUser = userRepository.findByIdentifier(user.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        findUser.updateUserTags(userTagsUpdateRequest.getTags());
        userRepository.save(findUser);
    }

    // 마이페이지 - 챌린지 현황
    public UserChallengeResultResponse getUserChallengeResult(User user) {
        User findUser = findUser(user.getIdentifier());
        HashMap<JoinResult, List<Long>> participantHashMap = new HashMap<>() {
            {
                put(FAIL, new ArrayList<>());
                put(PROCESSING, new ArrayList<>());
                put(SUCCESS, new ArrayList<>());
            }
        };
        List<ParticipantInfo> participantInfoList = findUser.getParticipantInfoList(); // 유저의 참여 정보를 담고 있는 리스트
        int participanTotalCount = participantInfoList.size();

        for (int i = 0; i < participantInfoList.size(); i++) { // 각 참여 정보를 받아옴.
            if (participantInfoList.get(i).getJoinStatus() == YES) {
                JoinResult joinResult = participantInfoList.get(i).getJoinResult();
                if (participantHashMap.containsKey(joinResult)) { // hashmap에 저장된 key와 일치 여부 확인
                    List<Long> participantIdList = participantHashMap.get(joinResult);
                    participantIdList.add(participantInfoList.get(i).getId()); // 일치한다면, 해당 key의 value인 list에 id 저장
                    participantHashMap.put(joinResult, participantIdList); // 최종적으로 hashmap에 저장
                }
            }
        }

        int fail = participantHashMap.get(FAIL).size();
        int success = participantHashMap.get(SUCCESS).size();
        int processing = participantHashMap.get(PROCESSING).size();

        return UserChallengeResultResponse.builder()
                .fail(fail)
                .success(success)
                .processing(processing)
                .beforeStart(participanTotalCount - fail - success - processing)
                .build();
    }


    private User findUser(String identifier) {
        return userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
