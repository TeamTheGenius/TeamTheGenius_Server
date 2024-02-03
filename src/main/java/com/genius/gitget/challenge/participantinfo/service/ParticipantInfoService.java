package com.genius.gitget.challenge.participantinfo.service;

import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantInfoRepository;
import com.genius.gitget.challenge.user.domain.User;
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
    private final InstanceRepository instanceRepository;

    @Transactional
    public ParticipantInfo joinNewInstance(User user, Long instanceId, String repositoryName) {
        Instance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException(INSTANCE_NOT_FOUND));

        ParticipantInfo participantInfo = ParticipantInfo.builder()
                .joinStatus(JoinStatus.YES)
                .joinResult(JoinResult.PROCESSING)
                .repositoryName(repositoryName)
                .build();
        participantInfo.setUserAndInstance(user, instance);
        participantInfoRepository.save(participantInfo);
        return participantInfo;
    }
}
