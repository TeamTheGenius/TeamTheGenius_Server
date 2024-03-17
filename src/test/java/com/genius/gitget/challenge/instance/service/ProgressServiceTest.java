package com.genius.gitget.challenge.instance.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.service.GithubService;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.scheduling.service.ProgressService;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles({"github"})
class ProgressServiceTest {
    @Autowired
    private InstanceDetailService instanceDetailService;
    @Autowired
    private ProgressService scheduleService;
    @Autowired
    private GithubService githubService;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CertificationRepository certificationRepository;
    @Autowired
    private InstanceProvider instanceProvider;

    @Value("${github.yeon-personalKey}")
    private String personalKey;
    @Value("${github.yeon-githubId}")
    private String githubId;
    @Value("${github.yeon-repository}")
    private String targetRepo;

    @Test
    @DisplayName("PRE_ACTIVITY 인스턴스들 중, 특정 조건에 해당하는 인스턴스들을 ACTIVITY로 상태를 바꿀 수 있다.")
    public void should_updateToActivity_when_conditionMatches() {
        //given
        LocalDate startedDate = LocalDate.of(2024, 3, 1);
        LocalDate completedDate = LocalDate.of(2024, 3, 30);
        LocalDate currentDate = LocalDate.of(2024, 3, 6);

        User user = getSavedUser("nickname1", githubId);
        Instance instance1 = getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);

        githubService.registerGithubPersonalToken(user, personalKey);
        instanceDetailService.joinNewChallenge(
                user,
                JoinRequest.builder()
                        .repository(targetRepo)
                        .instanceId(instance1.getId())
                        .build()
        );

        Participant participant1 = participantRepository.findByJoinInfo(user.getId(), instance1.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PARTICIPANT_NOT_FOUND));

        //when
        List<Instance> preActivities = instanceProvider.findAllByProgress(Progress.PREACTIVITY);
        assertThat(participant1.getJoinResult()).isEqualTo(JoinResult.READY);
        scheduleService.updateToActivity(currentDate);
        List<Instance> activities = instanceProvider.findAllByProgress(Progress.ACTIVITY);

        //then
        assertThat(preActivities.size()).isEqualTo(3);
        assertThat(activities.size()).isEqualTo(3);
        assertThat(participant1.getJoinResult()).isEqualTo(JoinResult.PROCESSING);
    }

    @Test
    @DisplayName("ACTIVITY 인스턴스들 중, 특정 조건에 해당하는 인스턴스들을 DONE 상태로 바꿀 수 있다.")
    public void should_updateToDone_when_conditionMatches() {
        //given
        LocalDate startedDate = LocalDate.of(2024, 3, 1);
        LocalDate completedDate = LocalDate.of(2024, 3, 30);
        LocalDate currentDate = LocalDate.of(2024, 4, 1);

        getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);

        //when
        List<Instance> activities = instanceProvider.findAllByProgress(Progress.PREACTIVITY);
        scheduleService.updateToDone(currentDate);
        List<Instance> done = instanceProvider.findAllByProgress(Progress.DONE);

        //then
        assertThat(activities.size()).isEqualTo(3);
        assertThat(done.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("DONE으로 상태를 바꾸면서 성공률이 85.5% 이상이라면 JoinResult를 SUCCESS로 변경한다.")
    public void should_updateToSuccess_then_rateOverThreshold() {
        //given
        LocalDate startedDate = LocalDate.of(2024, 3, 1);
        LocalDate completedDate = LocalDate.of(2024, 3, 2);
        LocalDate currentDate = LocalDate.of(2024, 4, 1);

        Instance instance = getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);

        Participant participant1 = getSavedParticipant(getSavedUser("nickname1"), instance);

        getSavedCertification(CertificateStatus.CERTIFICATED, startedDate, participant1);
        getSavedCertification(CertificateStatus.PASSED, completedDate, participant1);

        //when
        scheduleService.updateToDone(currentDate);

        //then
        List<Instance> done = instanceProvider.findAllByProgress(Progress.DONE);
        assertThat(done.size()).isEqualTo(3);
        assertThat(participant1.getJoinResult()).isEqualTo(JoinResult.SUCCESS);
    }

    @Test
    @DisplayName("DONE으로 상태를 바꾸면서 성공률이 85% 이하라면 JoinResult를 FAIL로 변경한다.")
    public void should_updateToFail_when_rateUnderThreshold() {
        //given
        LocalDate startedDate = LocalDate.of(2024, 3, 1);
        LocalDate completedDate = LocalDate.of(2024, 3, 2);
        LocalDate currentDate = LocalDate.of(2024, 4, 1);

        Instance instance = getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);

        Participant participant1 = getSavedParticipant(getSavedUser("nickname1"), instance);

        getSavedCertification(CertificateStatus.NOT_YET, startedDate, participant1);
        getSavedCertification(CertificateStatus.PASSED, completedDate, participant1);

        //when
        scheduleService.updateToDone(currentDate);

        //then
        List<Instance> done = instanceProvider.findAllByProgress(Progress.DONE);
        assertThat(done.size()).isEqualTo(3);
        assertThat(participant1.getJoinResult()).isEqualTo(JoinResult.FAIL);
    }

    private Instance getSavedInstance(LocalDate startedDate, LocalDate completedDate) {
        return instanceRepository.save(
                Instance.builder()
                        .title("title")
                        .progress(Progress.PREACTIVITY)
                        .pointPerPerson(100)
                        .startedDate(startedDate.atTime(0, 0))
                        .completedDate(completedDate.atTime(0, 0))
                        .build()
        );
    }

    private Participant getSavedParticipant(User user, Instance instance) {
        Participant participant = participantRepository.save(
                Participant.builder()
                        .joinResult(JoinResult.PROCESSING)
                        .joinStatus(JoinStatus.YES)
                        .build()
        );
        participant.setUserAndInstance(user, instance);
        return participant;
    }

    private User getSavedUser(String nickname, String githubId) {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname(nickname)
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier(githubId)
                        .information("information")
                        .tags("BE,FE")
                        .build()
        );
    }

    private User getSavedUser(String nickname) {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname(nickname)
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("identifier")
                        .information("information")
                        .tags("BE,FE")
                        .build()
        );
    }

    private Certification getSavedCertification(CertificateStatus status, LocalDate certificatedAt,
                                                Participant participant) {
        int attempt = DateUtil.getAttemptCount(participant.getStartedDate(), certificatedAt);
        Certification certification = Certification.builder()
                .certificationStatus(status)
                .currentAttempt(attempt)
                .certificatedAt(certificatedAt)
                .certificationLinks("certificationLink")
                .build();
        certification.setParticipant(participant);
        return certificationRepository.save(certification);
    }

}