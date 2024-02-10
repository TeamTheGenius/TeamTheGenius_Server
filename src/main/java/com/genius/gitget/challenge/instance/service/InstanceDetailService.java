package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantInfoRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InstanceDetailService {
    private final UserService userService;
    private final InstanceRepository instanceRepository;
    private final ParticipantInfoRepository participantInfoRepository;

    @Transactional
    public ParticipantInfo joinNewChallenge(User user, JoinRequest joinRequest) {
        User persistUser = userService.findUserById(user.getId());
        Instance instance = instanceRepository.findById(joinRequest.instanceId())
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));

        ParticipantInfo participantInfo = ParticipantInfo.builder()
                .joinStatus(JoinStatus.YES)
                .joinResult(JoinResult.PROCESSING)
                .repositoryName(joinRequest.repository())
                .build();
        participantInfo.setUserAndInstance(persistUser, instance);
        return participantInfoRepository.save(participantInfo);
    }
}
