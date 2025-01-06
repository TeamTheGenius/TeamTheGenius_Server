package com.genius.gitget.challenge.participant.service;

import static com.genius.gitget.global.util.exception.ErrorCode.PARTICIPANT_NOT_FOUND;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.exception.BusinessException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantService {
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
                .orElseThrow(() -> new BusinessException(PARTICIPANT_NOT_FOUND));
    }

    public Participant findById(Long participantInfoId) {
        return participantRepository.findById(participantInfoId)
                .orElseThrow(() -> new BusinessException(PARTICIPANT_NOT_FOUND));
    }

    public Slice<Participant> findAllByInstanceId(Long userId, Long instanceId, Pageable pageable) {
        Slice<Participant> participants = participantRepository.findAllByInstanceId(instanceId, JoinStatus.YES,
                pageable);
        List<Participant> filtered = participants.stream()
                .filter(participant -> participant.getUser().getId() != userId)
                .toList();

        return new SliceImpl<>(filtered, pageable, participants.hasNext());
    }

    public Instance getInstanceById(Long participantId) {
        return participantRepository.findById(participantId)
                .orElseThrow(() -> new BusinessException(PARTICIPANT_NOT_FOUND))
                .getInstance();
    }

    public List<Participant> findJoinedByProgress(Long userId, Progress progress) {
        return participantRepository.findAllByStatus(userId, progress, JoinStatus.YES);
    }

    public List<Participant> findDoneInstances(Long userId) {
        List<Participant> doneInstances = new ArrayList<>();
        // 실패한 챌린지 리스트
        doneInstances.addAll(participantRepository.findAllByStatus(userId, Progress.ACTIVITY, JoinStatus.NO));

        // 성공한 챌린지 리스트
        doneInstances.addAll(participantRepository.findAllByStatus(userId, Progress.DONE, JoinStatus.YES));

        return doneInstances;
    }

    public boolean hasJoinedParticipant(Long userId, Long instanceId) {
        return participantRepository.findByJoinInfo(userId, instanceId)
                .map(participant -> participant.getJoinStatus() != JoinStatus.NO)
                .orElse(false);
    }

    @Transactional
    public void getRewards(Participant participant, int rewardPoints) {
        User user = participant.getUser();

        participant.validateRewardCondition();
        user.updatePoints((long) rewardPoints);
        participant.getRewards(rewardPoints);
    }
}
