package com.genius.gitget.challenge.participantinfo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${github.personalKey}")
    private String personalKey;
    @Value("${github.githubId}")
    private String githubId;
    @Value("${github.repository}")
    private String targetRepo;


    @Test
    @DisplayName("사용자가 인스턴스에 참여할 때, 레포지토리 이름을 전달받아 저장한다.")
    public void should_saveParticipantInfo_when_userJoinInstance() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();

        //when
        ParticipantInfo participantInfo = participantInfoService.joinNewInstance(user, instance.getId(), targetRepo);

        //then
        assertThat(participantInfo.getJoinStatus()).isEqualTo(JoinStatus.YES);
        assertThat(participantInfo.getJoinResult()).isEqualTo(JoinResult.PROCESSING);
        assertThat(participantInfo.getRepositoryName()).isEqualTo(targetRepo);
        assertThat(participantInfo.getUser().getIdentifier()).isEqualTo(githubId);
        assertThat(participantInfo.getInstance().getProgress()).isEqualTo(Progress.PREACTIVITY);
    }

    @Test
    @DisplayName("사용자가 인스턴스에 참여할 때, 인스턴스가 존재하지 않는다면 예외가 발생한다.")
    public void should_throwException_when_instanceNotExist() {
        //given
        User user = getSavedUser(githubId);

        //when & then
        assertThatThrownBy(() -> participantInfoService.joinNewInstance(user, 1L, targetRepo))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INSTANCE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("참여정보 DB에 저장된 레포지토리 이름을 받아올 수 있다.")
    public void should_returnRepoName_when_participantExist() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();

        //when
        participantInfoService.joinNewInstance(user, instance.getId(), targetRepo);
        String repositoryName = participantInfoService.getRepositoryName(user.getId(), instance.getId());

        //then
        assertThat(repositoryName).isEqualTo(targetRepo);
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
                        .build()
        );
    }
}