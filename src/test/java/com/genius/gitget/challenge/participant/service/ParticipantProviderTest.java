package com.genius.gitget.challenge.participant.service;

import static com.genius.gitget.challenge.instance.domain.Progress.ACTIVITY;
import static com.genius.gitget.challenge.instance.domain.Progress.PREACTIVITY;
import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ParticipantProviderTest {
    @Autowired
    ParticipantProvider participantProvider;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    ParticipantRepository participantRepository;


    @Test
    @DisplayName("userId와 instanceId를 통해 저장되어 있는 ParticipantInfo를 받아올 수 있다.")
    public void should_getParticipantInfo_when_passUserIdAndInstanceId() {
        //given
        User savedUser = getSavedUser();
        Instance savedInstance = getSavedInstance(PREACTIVITY);
        getSavedParticipant(savedUser, savedInstance);

        //when
        Participant participant = participantProvider.findByJoinInfo(savedUser.getId(),
                savedInstance.getId());

        //then
        assertThat(participant.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(participant.getInstance().getId()).isEqualTo(savedInstance.getId());
    }

    @Test
    @DisplayName("Participant를 Participant의 PK를 통해 찾을 수 있다.")
    public void should_getParticipant_when_passPK() {
        //given
        User savedUser = getSavedUser();
        Instance savedInstance = getSavedInstance(PREACTIVITY);
        Participant participant = getSavedParticipant(savedUser, savedInstance);

        //when
        Participant foundParticipant = participantProvider.findById(participant.getId());

        //then
        assertThat(foundParticipant.getId()).isEqualTo(participant.getId());
        assertThat(foundParticipant.getJoinStatus()).isEqualTo(participant.getJoinStatus());
        assertThat(foundParticipant.getUser()).isEqualTo(savedUser);
        assertThat(foundParticipant.getInstance()).isEqualTo(savedInstance);
    }

    @Test
    @DisplayName("Participant들 중 Progress(진행 상황)과 사용자 정보 조건에 맞는 정보들을 불러올 수 있다.")
    public void should_returnList_when_passProgress() {
        //given
        User user = getSavedUser();
        Instance instance1 = getSavedInstance(PREACTIVITY);
        Instance instance2 = getSavedInstance(PREACTIVITY);
        Instance instance3 = getSavedInstance(ACTIVITY);
        Participant participant1 = getSavedParticipant(user, instance1);
        Participant participant2 = getSavedParticipant(user, instance2);
        Participant participant3 = getSavedParticipant(user, instance3);

        //when
        List<Participant> participants = participantProvider.findJoinedByProgress(user.getId(), PREACTIVITY);

        //then
        assertThat(participants.size()).isEqualTo(2);
    }


    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("identifier")
                        .information("information")
                        .tags("BE,FE")
                        .build()
        );
    }

    private Instance getSavedInstance(Progress progress) {
        return instanceRepository.save(
                Instance.builder()
                        .progress(progress)
                        .build()
        );
    }

    private Participant getSavedParticipant(User user, Instance instance) {
        Participant participant = Participant.builder()
                .joinStatus(JoinStatus.YES)
                .build();
        participant.setUserAndInstance(user, instance);
        return participantRepository.save(participant);
    }
}