package com.genius.todoffin.participantInfo;

import com.genius.todoffin.participantinfo.domain.JoinResult;
import com.genius.todoffin.participantinfo.domain.JoinStatus;
import com.genius.todoffin.participantinfo.domain.ParticipantInfo;
import com.genius.todoffin.participantinfo.repository.ParticipantInfoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class ParticipantInfoRepositoryTest {

    @Autowired
    private ParticipantInfoRepository participantInfoRepository;

    @Test
    public void 참여자_정보_저장() {
        ParticipantInfo participantInfo = new ParticipantInfo(JoinStatus.YES, JoinResult.SUCCESS);

        ParticipantInfo savedInfo = participantInfoRepository.save(participantInfo);

        Assertions.assertThat(participantInfo.getId()).isEqualTo(savedInfo.getId());
    }
}
