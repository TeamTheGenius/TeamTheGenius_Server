package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_JOIN_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_QUIT_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.PARTICIPANT_INFO_NOT_FOUND;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.dto.detail.JoinResponse;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
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
    public JoinResponse joinNewChallenge(User user, JoinRequest joinRequest) {
        User persistUser = userService.findUserById(user.getId());
        Instance instance = instanceRepository.findById(joinRequest.instanceId())
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));

        if (!instance.canJoinInstance()) {
            throw new BusinessException(CAN_NOT_JOIN_INSTANCE);
        }

        instance.updateParticipantCount(1);
        ParticipantInfo participantInfo = ParticipantInfo.createDefaultParticipantInfo(joinRequest.repository());
        participantInfo.setUserAndInstance(persistUser, instance);
        return JoinResponse.createJoinResponse(participantInfoRepository.save(participantInfo));
    }

    //TODO: 코드 리팩터링
    @Transactional
    public JoinResponse quitChallenge(User user, Long instanceId) {
        Instance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));
        ParticipantInfo participantInfo = participantInfoRepository.findByJoinInfo(user.getId(), instance.getId())
                .orElseThrow(() -> new BusinessException(PARTICIPANT_INFO_NOT_FOUND));

        if (instance.getProgress() == Progress.DONE) {
            throw new BusinessException(CAN_NOT_QUIT_INSTANCE);
        }

        if (instance.getProgress() == Progress.PREACTIVITY) {
            instance.updateParticipantCount(-1);
            participantInfoRepository.delete(participantInfo);
            return JoinResponse.createQuitResponse();
        }

        instance.updateParticipantCount(-1);
        participantInfo.quitInstance();
        return JoinResponse.createJoinResponse(participantInfo);
    }
}
