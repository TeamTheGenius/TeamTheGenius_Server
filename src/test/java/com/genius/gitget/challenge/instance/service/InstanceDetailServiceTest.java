package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class InstanceDetailServiceTest {
    @Autowired
    InstanceDetailService instanceDetailService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;

    @Value("${github.personalKey}")
    private String personalKey;
    @Value("${github.githubId}")
    private String githubId;
    @Value("${github.repository}")
    private String targetRepo;


    @Test
    @DisplayName("챌린지 참여에 필요한 정보를 전달했을 때, 참여 정보가 저장이 되어야 한다.")
    public void should_saveParticipantInfo_when_passInfo() {
        //given
        User savedUser = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        JoinRequest joinRequest = JoinRequest.builder()
                .instanceId(instance.getId())
                .repository(targetRepo)
                .build();

        //when
        ParticipantInfo participantInfo = instanceDetailService.joinNewChallenge(savedUser, joinRequest);

        //then
        assertThat(participantInfo.getInstance().getId()).isEqualTo(instance.getId());
        assertThat(participantInfo.getRepositoryName()).isEqualTo(targetRepo);
        assertThat(participantInfo.getJoinStatus()).isEqualTo(JoinStatus.YES);
        assertThat(participantInfo.getJoinResult()).isEqualTo(JoinResult.PROCESSING);
    }

    @Test
    @DisplayName("챌린지 참여 요청을 했을 때, 인스턴스가 존재하지 않는다면 예외가 발생한다.")
    public void should_throwException_when_instanceNotExist() {
        //given
        User savedUser = getSavedUser(githubId);
        JoinRequest joinRequest = JoinRequest.builder()
                .instanceId(1L)
                .repository(targetRepo)
                .build();

        //when & then
        assertThatThrownBy(() -> instanceDetailService.joinNewChallenge(savedUser, joinRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(INSTANCE_NOT_FOUND.getMessage());
    }

    private User getSavedUser(String githubId) {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier(githubId)
                        .information("information")
                        .tags("BE,FE")
                        .build()
        );
    }

    private Instance getSavedInstance() {
        return instanceRepository.save(
                Instance.builder()
                        .progress(Progress.PREACTIVITY)
                        .startedDate(LocalDateTime.of(2024, 2, 1, 11, 3))
                        .build()
        );
    }
}