package com.genius.gitget.challenge.participantinfo.service;

import static com.genius.gitget.global.util.exception.ErrorCode.PARTICIPANT_INFO_NOT_FOUND;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantInfoRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantInfoService {
    private final ParticipantInfoRepository participantInfoRepository;


    public ParticipantInfo findByJoinInfo(Long userId, Long instanceId) {
        return participantInfoRepository.findByJoinInfo(userId, instanceId)
                .orElseThrow(() -> new BusinessException(PARTICIPANT_INFO_NOT_FOUND));
    }

    public ParticipantInfo findById(Long participantInfoId) {
        return participantInfoRepository.findById(participantInfoId)
                .orElseThrow(() -> new BusinessException(PARTICIPANT_INFO_NOT_FOUND));
    }

    public Instance getInstanceById(Long participantId) {
        return participantInfoRepository.findById(participantId)
                .orElseThrow(() -> new BusinessException(PARTICIPANT_INFO_NOT_FOUND))
                .getInstance();
    }

    public boolean hasParticipantInfo(Long userId, Long instanceId) {
        return participantInfoRepository.findByJoinInfo(userId, instanceId).isPresent();
    }

}
