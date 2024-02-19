package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_JOIN_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_QUIT_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.INSTANCE_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.PARTICIPANT_INFO_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.detail.InstanceResponse;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.dto.detail.JoinResponse;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participantinfo.domain.JoinResult;
import com.genius.gitget.challenge.participantinfo.domain.JoinStatus;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.repository.ParticipantInfoRepository;
import com.genius.gitget.challenge.participantinfo.service.ParticipantInfoService;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
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
    ParticipantInfoService participantInfoService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    ParticipantInfoRepository participantInfoRepository;

    @Value("${github.githubId}")
    private String githubId;
    @Value("${github.repository}")
    private String targetRepo;


    @Test
    @DisplayName("챌린지 참여에 필요한 정보를 전달했을 때, 참여 정보가 저장이 되어야 한다.")
    public void should_saveParticipantInfo_when_passInfo() {
        //given
        User savedUser = getSavedUser(githubId);
        Instance instance = getSavedInstance(Progress.PREACTIVITY);
        JoinRequest joinRequest = JoinRequest.builder()
                .instanceId(instance.getId())
                .repository(targetRepo)
                .build();

        //when
        JoinResponse joinResponse = instanceDetailService.joinNewChallenge(savedUser, joinRequest);

        //then
        assertThat(joinResponse.joinStatus()).isEqualTo(JoinStatus.YES);
        assertThat(joinResponse.joinResult()).isEqualTo(JoinResult.PROCESSING);
        assertThat(instance.getParticipantCount()).isEqualTo(1);
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

    @ParameterizedTest
    @DisplayName("챌린지 참여 요청을 했을 때, 인스턴스의 상태가 시작 전이 아니라면 예외가 발생한다.")
    @EnumSource(mode = Mode.INCLUDE, names = {"ACTIVITY", "DONE"})
    public void should_throwException_when_instanceProgressNotPreactivity(Progress progress) {
        //given
        User savedUser = getSavedUser(githubId);
        Instance savedInstance = getSavedInstance(progress);
        JoinRequest joinRequest = JoinRequest.builder()
                .repository(targetRepo)
                .instanceId(savedInstance.getId())
                .build();

        //when & then
        assertThatThrownBy(() -> instanceDetailService.joinNewChallenge(savedUser, joinRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CAN_NOT_JOIN_INSTANCE.getMessage());
    }

    @Test
    @DisplayName("챌린지 참여 요청을 했을 때, 사용자가 이미 참여한 챌린지인 경우 예외가 발생한다.")
    public void should_throwException_when_userAlreadyJoined() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance(Progress.PREACTIVITY);
        JoinRequest joinRequest = JoinRequest.builder()
                .repository(targetRepo)
                .instanceId(instance.getId())
                .build();

        //when
        instanceDetailService.joinNewChallenge(user, joinRequest);

        //then
        assertThatThrownBy(() -> instanceDetailService.joinNewChallenge(user, joinRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CAN_NOT_JOIN_INSTANCE.getMessage());
    }

    @Test
    @DisplayName("아직 시작하지 않은 챌린지에 대해 취소 요청을 하면 ParticipantInfo가 삭제된다.")
    public void should_joinStatusIsNo_when_quitChallenge() {
        //given
        User savedUser = getSavedUser(githubId);
        Instance savedInstance = getSavedInstance(Progress.PREACTIVITY);

        //when
        instanceDetailService.joinNewChallenge(savedUser, new JoinRequest(savedInstance.getId(), targetRepo));
        JoinResponse joinResponse = instanceDetailService.quitChallenge(savedUser, savedInstance.getId());
        Optional<ParticipantInfo> byJoinInfo = participantInfoRepository.findByJoinInfo(savedUser.getId(),
                savedInstance.getId());

        //then
        assertThat(byJoinInfo).isEmpty();
        assertThat(savedInstance.getParticipantCount()).isEqualTo(0);
        assertThat(joinResponse.participantId()).isEqualTo(null);
        assertThat(joinResponse.joinResult()).isEqualTo(null);
        assertThat(joinResponse.joinStatus()).isEqualTo(null);
    }

    @Test
    @DisplayName("진행 중인 챌린지에 대해 취소 요청을 하면 인스턴스의 참여 인원 수가 줄어들고, 참여 정보가 변경된다")
    public void should_changeParticipantInfo_when_requestQuitInstance() {
        //given
        User savedUser = getSavedUser(githubId);
        Instance savedInstance = getSavedInstance(Progress.PREACTIVITY);
        JoinRequest joinRequest = JoinRequest.builder()
                .instanceId(savedInstance.getId())
                .repository(targetRepo)
                .build();

        //when
        instanceDetailService.joinNewChallenge(savedUser, joinRequest);
        savedInstance.updateProgress(Progress.ACTIVITY);
        instanceDetailService.quitChallenge(savedUser, savedInstance.getId());
        ParticipantInfo participantInfo = participantInfoService.findByJoinInfo(savedUser.getId(),
                savedInstance.getId());

        //then
        assertThat(savedInstance.getParticipantCount()).isEqualTo(0);
        assertThat(participantInfo.getJoinResult()).isEqualTo(JoinResult.FAIL);
        assertThat(participantInfo.getJoinStatus()).isEqualTo(JoinStatus.NO);
    }

    @Test
    @DisplayName("챌린지 취소 요청을 할 때 인스턴스가 존재하지 않으면 예외가 발생한다.")
    public void should_throwException_when_instanceNotExist_quitChallenge() {
        //given
        User savedUser = getSavedUser(githubId);

        //when & then
        assertThatThrownBy(() -> instanceDetailService.quitChallenge(savedUser, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(INSTANCE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("챌린지 취소 요청을 할 때 참여 정보가 존재하지 않으면 예외가 발생한다.")
    public void should_throwException_when_participantInfoNotExist() {
        //given
        User savedUser = getSavedUser(githubId);
        Instance savedInstance = getSavedInstance(Progress.PREACTIVITY);

        //when & then
        assertThatThrownBy(() -> instanceDetailService.quitChallenge(savedUser, savedInstance.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(PARTICIPANT_INFO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("챌린지 취소 요청을 할 때, 인스턴스의 진행 상황이 DONE이면 예외가 발생한다.")
    public void should_throwException_when_progressIsDONE() {
        //given
        User savedUser = getSavedUser(githubId);
        Instance savedInstance = getSavedInstance(Progress.PREACTIVITY);
        JoinRequest joinRequest = JoinRequest.builder()
                .instanceId(savedInstance.getId())
                .repository(targetRepo)
                .build();

        //when
        instanceDetailService.joinNewChallenge(savedUser, joinRequest);
        savedInstance.updateProgress(Progress.DONE);

        //then
        assertThatThrownBy(() -> instanceDetailService.quitChallenge(savedUser, savedInstance.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CAN_NOT_QUIT_INSTANCE.getMessage());
    }

    @Test
    @DisplayName("사용자가 참여한 인스턴스에 대해 상세 조회를 하면 상세 페이지에 필요한 데이터들을 반환해야 한다.")
    public void should_returnValues_when_joinedInstance() {
        //given
        User savedUser = getSavedUser(githubId);
        Instance savedInstance = getSavedInstance(Progress.PREACTIVITY, LocalDate.now().plusDays(2));
        JoinRequest joinRequest = JoinRequest.builder()
                .instanceId(savedInstance.getId())
                .repository(targetRepo)
                .build();

        //when
        instanceDetailService.joinNewChallenge(savedUser, joinRequest);
        InstanceResponse instanceResponse = instanceDetailService.getInstanceDetailInformation(savedUser,
                savedInstance.getId());

        //then
        assertThat(instanceResponse.instanceId()).isEqualTo(savedInstance.getId());
        assertThat(instanceResponse.remainDays()).isEqualTo(2);
        assertThat(instanceResponse.participantCount()).isEqualTo(1);
        assertThat(instanceResponse.pointPerPerson()).isEqualTo(100);
        assertThat(instanceResponse.description()).isEqualTo(savedInstance.getDescription());
        assertThat(instanceResponse.joinStatus()).isEqualTo(JoinStatus.YES);
        assertThat(instanceResponse.hitCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자가 참여하지 않은 챌린지에 대해 상세 조회를 하면 상세 페이지에 필요한 정보들을 반환할 수 있다.")
    public void should_returnData_when_notJoinedInstance() {
        //given
        User savedUser = getSavedUser(githubId);
        Instance savedInstance = getSavedInstance(Progress.PREACTIVITY, LocalDate.now().plusDays(2));

        //when
        InstanceResponse instanceResponse = instanceDetailService.getInstanceDetailInformation(savedUser,
                savedInstance.getId());

        //then
        assertThat(instanceResponse.instanceId()).isEqualTo(savedInstance.getId());
        assertThat(instanceResponse.remainDays()).isEqualTo(2);
        assertThat(instanceResponse.participantCount()).isEqualTo(0);
        assertThat(instanceResponse.pointPerPerson()).isEqualTo(100);
        assertThat(instanceResponse.description()).isEqualTo(savedInstance.getDescription());
        assertThat(instanceResponse.joinStatus()).isEqualTo(JoinStatus.NO);
        assertThat(instanceResponse.hitCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("상세 정보를 요청하는 인스턴스가 존재하지 않는다면 예외가 발생해야 한다.")
    public void should_throwException_when_requestDetail_instanceNotExist() {
        //given
        User savedUser = getSavedUser(githubId);

        //when & then
        assertThatThrownBy(() -> instanceDetailService.getInstanceDetailInformation(savedUser, 1L))
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

    private Instance getSavedInstance(Progress progress) {
        return instanceRepository.save(
                Instance.builder()
                        .progress(progress)
                        .startedDate(LocalDateTime.of(2024, 2, 1, 11, 3))
                        .build()
        );
    }

    private Instance getSavedInstance(Progress progress, LocalDate startedDate) {
        return instanceRepository.save(
                Instance.builder()
                        .progress(progress)
                        .description("description")
                        .notice("notice")
                        .certificationMethod("certification method")
                        .pointPerPerson(100)
                        .startedDate(startedDate.atTime(12, 12))
                        .completedDate(startedDate.plusDays(30).atTime(12, 12))
                        .build()
        );
    }
}