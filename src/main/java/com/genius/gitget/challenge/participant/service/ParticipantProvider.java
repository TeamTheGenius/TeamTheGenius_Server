package com.genius.gitget.challenge.participant.service;

import static com.genius.gitget.global.util.exception.ErrorCode.PARTICIPANT_INFO_NOT_FOUND;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantProvider {
    private final ParticipantRepository participantRepository;

    @Transactional
    public Participant save(Participant participant) {
        return participantRepository.save(participant);
    }

    @Transactional
    public void delete(Participant participant) {
        participantRepository.delete(participant);
    }

    public Participant findByJoinInfo(Long userId, Long instanceId) {
        return participantRepository.findByJoinInfo(userId, instanceId)
                .orElseThrow(() -> new BusinessException(PARTICIPANT_INFO_NOT_FOUND));
    }

    public Participant findById(Long participantInfoId) {
        return participantRepository.findById(participantInfoId)
                .orElseThrow(() -> new BusinessException(PARTICIPANT_INFO_NOT_FOUND));
    }

    public Slice<Participant> findAllByInstanceId(Long instanceId, Pageable pageable) {
        return participantRepository.findAllByInstanceId(instanceId, JoinStatus.YES, pageable);
    }

    public Instance getInstanceById(Long participantId) {
        return participantRepository.findById(participantId)
                .orElseThrow(() -> new BusinessException(PARTICIPANT_INFO_NOT_FOUND))
                .getInstance();
    }

    public List<Participant> findJoinedByProgress(Long userId, Progress progress) {
        return participantRepository.findAllJoinedByProgress(userId, progress);
    }

    public boolean hasParticipant(Long userId, Long instanceId) {
        return participantRepository.findByJoinInfo(userId, instanceId).isPresent();
    }

    public LocalDate getInstanceStartDate(Long participantId) {
        return getInstanceById(participantId).getStartedDate().toLocalDate();
    }

}
