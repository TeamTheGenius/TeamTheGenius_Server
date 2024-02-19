package com.genius.gitget.challenge.participantinfo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantInfoRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ParticipantInfoServiceTest {
    @Autowired
    ParticipantInfoService participantInfoService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    ParticipantInfoRepository participantInfoRepository;


    @Test
    @DisplayName("userId와 instanceId를 통해 저장되어 있는 ParticipantInfo를 받아올 수 있다.")
    public void should_getParticipantInfo_when_passUserIdAndInstanceId() {
        //given
        User savedUser = getSavedUser();
        Instance savedInstance = getSavedInstance();
        getSavedParticipantInfo(savedUser, savedInstance);

        //when
        ParticipantInfo participantInfo = participantInfoService.findByJoinInfo(savedUser.getId(),
                savedInstance.getId());

        //then
        assertThat(participantInfo.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(participantInfo.getInstance().getId()).isEqualTo(savedInstance.getId());
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

    private Instance getSavedInstance() {
        return instanceRepository.save(
                Instance.builder()
                        .progress(Progress.PREACTIVITY)
                        .build()
        );
    }

    private ParticipantInfo getSavedParticipantInfo(User user, Instance instance) {
        ParticipantInfo participantInfo = ParticipantInfo.builder()
                .joinStatus(JoinStatus.YES)
                .build();
        participantInfo.setUserAndInstance(user, instance);
        return participantInfoRepository.save(participantInfo);
    }
}