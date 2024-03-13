package com.genius.gitget.profile.service;

import static com.genius.gitget.challenge.participant.domain.JoinResult.FAIL;
import static com.genius.gitget.challenge.participant.domain.JoinResult.PROCESSING;
import static com.genius.gitget.challenge.participant.domain.JoinResult.SUCCESS;
import static com.genius.gitget.challenge.participant.domain.JoinStatus.YES;

import com.genius.gitget.admin.signout.Signout;
import com.genius.gitget.admin.signout.SignoutRepository;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.file.repository.FilesRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
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
    private final SignoutRepository signoutRepository;

    private static boolean isProfileFileType(Files files) {
        return files != null && files.getFileType().equals(FileType.PROFILE);
    }

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
        Files files = getFiles(findUser);
        if (isProfileFileType(files)) {
            return UserInformationResponse.createByEntity(findUser, files);
        } else {
            return UserInformationResponse.createByEntity(findUser, null);
        }
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
        Files files = getFiles(findUser);
        if (isProfileFileType(files)) {
            return UserDetailsInformationResponse.createByEntity(findUser, files, participantCount);
        } else {
            return UserDetailsInformationResponse.createByEntity(findUser, null, participantCount);
        }
    }

    // 마이페이지 - 사용자 정보 수정
    @Transactional
    public void updateUserInformation(User user, UserInformationUpdateRequest userInformationUpdateRequest,
                                      MultipartFile multipartFile, String type) {
        User findUser = getUserByIdentifier(user.getIdentifier());
        findUser.updateUserInformation(
                userInformationUpdateRequest.getNickname(),
                userInformationUpdateRequest.getInformation());

        if (multipartFile != null) {
            if (findUser.getFiles().isEmpty()) {
                Files uploadedFile = filesService.uploadFile(multipartFile, type);
                findUser.setFiles(uploadedFile);
            } else {
                filesService.updateFile(findUser.getFiles().get().getId(), multipartFile);
            }
        }
        userRepository.save(findUser);
    }

    // 마이페이지 - 회원 탈퇴
    @Transactional
    public void deleteUserInformation(User user, String reason) {
        User findUser = getUserByIdentifier(user.getIdentifier());
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
                put(FAIL, new ArrayList<>());
                put(PROCESSING, new ArrayList<>());
                put(SUCCESS, new ArrayList<>());
            }
        };
        List<Participant> participantInfoList = findUser.getParticipantList(); // 유저의 참여 정보를 담고 있는 리스트
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

    private User getUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Files getFiles(User findUser) {
        if (findUser.getFiles().isPresent()) {
            return filesRepository.findById(findUser.getFiles().get().getId()).orElse(null);
        } else {
            return null;
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
