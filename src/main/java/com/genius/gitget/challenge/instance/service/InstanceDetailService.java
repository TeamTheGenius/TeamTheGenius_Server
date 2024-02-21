package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_JOIN_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_QUIT_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.detail.InstanceResponse;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.dto.detail.JoinResponse;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.Participant;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantRepository;
import com.genius.gitget.challenge.participantinfo.service.ParticipantProvider;
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
    private final ParticipantProvider participantProvider;
    //REFACTOR: ParticipantInfoService 에만 의존하도록 리팩토링할 것
    private final ParticipantRepository participantRepository;


    public InstanceResponse getInstanceDetailInformation(User user, Long instanceId) {
        Instance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));
        if (participantProvider.hasParticipant(user.getId(), instanceId)) {
            return InstanceResponse.createByEntity(instance, JoinStatus.YES);
        }

        return InstanceResponse.createByEntity(instance, JoinStatus.NO);
    }

    @Transactional
    public JoinResponse joinNewChallenge(User user, JoinRequest joinRequest) {
        User persistUser = userService.findUserById(user.getId());
        Instance instance = instanceRepository.findById(joinRequest.instanceId())
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));

        if (canJoinChallenge(persistUser, instance)) {
            throw new BusinessException(CAN_NOT_JOIN_INSTANCE);
        }

        instance.updateParticipantCount(1);
        Participant participant = Participant.createDefaultParticipantInfo(joinRequest.repository());
        participant.setUserAndInstance(persistUser, instance);
        return JoinResponse.createJoinResponse(participantRepository.save(participant));
    }

    private boolean canJoinChallenge(User user, Instance instance) {
        return (instance.getProgress() != Progress.PREACTIVITY) ||
                participantProvider.hasParticipant(user.getId(), instance.getId());
    }

    @Transactional
    public JoinResponse quitChallenge(User user, Long instanceId) {
        Instance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));
        Participant participant = participantProvider.findByJoinInfo(user.getId(), instanceId);

        if (instance.getProgress() == Progress.DONE) {
            throw new BusinessException(CAN_NOT_QUIT_INSTANCE);
        }

        if (instance.getProgress() == Progress.PREACTIVITY) {
            instance.updateParticipantCount(-1);
            participantRepository.delete(participant);
            return JoinResponse.createQuitResponse();
        }

        instance.updateParticipantCount(-1);
        participant.quitChallenge();
        return JoinResponse.createJoinResponse(participant);
    }
}
