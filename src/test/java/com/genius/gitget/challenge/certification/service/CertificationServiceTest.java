package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.PASSED;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_TOKEN_NOT_FOUND;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_CERTIFICATE_PERIOD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationInformation;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.InstancePreviewResponse;
import com.genius.gitget.challenge.certification.dto.TotalResponse;
import com.genius.gitget.challenge.certification.dto.WeekResponse;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import com.genius.gitget.challenge.item.repository.ItemRepository;
import com.genius.gitget.challenge.item.repository.UserItemRepository;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles({"github"})
class CertificationServiceTest {
    @Autowired
    private CertificationService certificationService;
    @Autowired
    private GithubService githubService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private CertificationRepository certificationRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserItemRepository userItemRepository;

    @Value("${github.yeon-personalKey}")
    private String personalKey;

    @Value("${github.yeon-githubId}")
    private String githubId;

    @Value("${github.yeon-repository}")
    private String targetRepo;


    @Test
    @DisplayName("사용자가 연결한 레포지토리에 특정 날짜의 PR이 있으면 인증으로 간주한다")
    public void should_certificate_when_prExist() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        getSavedParticipant(user, instance);
        githubService.registerGithubPersonalToken(user, personalKey);

        LocalDate targetDate = LocalDate.of(2024, 2, 5);

        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(targetDate)
                .build();
        instance.updateProgress(Progress.ACTIVITY);

        //when
        CertificationResponse certificationResponse = certificationService.updateCertification(user,
                certificationRequest);
        Certification certification = certificationRepository.findById(certificationResponse.certificationId())
                .get();

        //then
        assertThat(certification.getId()).isEqualTo(certificationResponse.certificationId());
        assertThat(certificationResponse.certificateStatus()).isEqualTo(CERTIFICATED);
        assertThat(certificationResponse.certificatedAt()).isEqualTo(targetDate);
        assertThat(certificationResponse.prCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("인증을 시도한 날짜가 챌린지의 진행 기간과 겹치지 않는다면 예외를 발생한다.")
    public void should_throwException_when_progressIsNotActivity() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        getSavedParticipant(user, instance);
        githubService.registerGithubPersonalToken(user, personalKey);

        LocalDate targetDate = LocalDate.of(2024, 12, 6);

        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(targetDate)
                .build();
        instance.updateProgress(Progress.ACTIVITY);

        //when && then
        assertThatThrownBy(() -> certificationService.updateCertification(user, certificationRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_CERTIFICATE_PERIOD.getMessage());
    }

    @Test
    @DisplayName("사용자가 연결한 레포지토리에 특정 날짜의 PR이 존재하지 않으면 인증이 아직 안된 것으로 간주한다.")
    public void should_notCertificate_when_prNotExist() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        getSavedParticipant(user, instance);
        githubService.registerGithubPersonalToken(user, personalKey);

        LocalDate targetDate = LocalDate.of(2024, 2, 6);

        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(targetDate)
                .build();
        instance.updateProgress(Progress.ACTIVITY);

        //when
        CertificationResponse certificationResponse = certificationService.updateCertification(user,
                certificationRequest);
        Certification certification = certificationRepository.findById(certificationResponse.certificationId())
                .get();

        //then
        assertThat(certification.getId()).isEqualTo(certificationResponse.certificationId());
        assertThat(certificationResponse.certificateStatus()).isEqualTo(CertificateStatus.NOT_YET);
        assertThat(certificationResponse.certificatedAt()).isEqualTo(targetDate);
        assertThat(certificationResponse.prCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("github를 통해 public repository 정보들을 받아올 수 있다.")
    public void should_returnRepositoryList_when_passGitHubToken() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        githubService.registerGithubPersonalToken(user, personalKey);

        //when
        List<String> repositoryList = githubService.getPublicRepositories(user);

        //then
        assertThat(repositoryList.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("repository 정보를 불러올 때 github token이 제대로 설정되어있지 않다면 예외를 발생해야 한다.")
    public void should_throwException_when_loadRepository() {
        //given
        User user = getSavedUser(githubId);

        //when & then
        assertThatThrownBy(() -> githubService.getPublicRepositories(user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("특정 사용자가 일주일 간 인증한 현황들을 받아올 수 있다.")
    public void should_returnCertifications_when_passDuration() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 2, 3);
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 4);
        Participant participant = getSavedParticipant(getSavedUser(githubId), getSavedInstance());

        //when
        getSavedCertification(NOT_YET, startDate, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, endDate.minusDays(1), participant);
        getSavedCertification(CERTIFICATED, endDate, participant);

        List<CertificationResponse> weekCertification = certificationService.getWeekCertification(
                participant.getId(), currentDate);

        //then
        assertThat(weekCertification.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("특정 사용자가 일주일 간 인증한 현황들을 받아올 때, 더미 데이터를 포함하여 연속적인 데이터로 받아올 수 있다.")
    public void should_returnList_when_dataIsNotContinuous() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 29);
        LocalDate currentDate = LocalDate.of(2024, 2, 8);

        Participant participant = getSavedParticipant(getSavedUser(githubId), getSavedInstance());

        //when
        getSavedCertification(NOT_YET, startDate, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(4), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(6), participant);

        List<CertificationResponse> weekCertification = certificationService.getWeekCertification(
                participant.getId(), currentDate);

        //then
        assertThat(weekCertification.size()).isEqualTo(4);
        assertThat(weekCertification.get(0).certificationAttempt()).isEqualTo(1);
        assertThat(weekCertification.get(1).certificationAttempt()).isEqualTo(2);
        assertThat(weekCertification.get(2).certificationAttempt()).isEqualTo(3);
        assertThat(weekCertification.get(3).certificationAttempt()).isEqualTo(4);
    }

    @Test
    @DisplayName("현재 일자까지의 인증 현황들을 받아올 수 있다.")
    public void should_returnList_when_passDate() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 29);
        LocalDate currentDate = LocalDate.of(2024, 2, 8);

        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(getSavedUser(githubId), instance);

        //when
        getSavedCertification(NOT_YET, startDate, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(4), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(6), participant);

        TotalResponse totalResponse = certificationService.getTotalCertification(participant.getId(),
                currentDate);

        //then
        assertThat(totalResponse.certifications().size()).isEqualTo(8);
        assertThat(totalResponse.totalAttempts()).isEqualTo(instance.getTotalAttempt());
    }

    @Test
    @DisplayName("사용자가 참여한 챌린지에 대한 상세 정보를 받을 수 있다.")
    public void should_returnDetailInfo_when_participate() {
        //given
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);

        //when
        InstancePreviewResponse instancePreviewResponse = certificationService.getInstancePreview(instance.getId());

        //then
        assertThat(instancePreviewResponse.instanceId()).isEqualTo(instance.getId());
    }

    @Test
    @DisplayName("사용자가 참여한 챌린지가 아직 시작 전이라면, 성공/실패의 값이 모두 0이어야한다.")
    public void should_getInformation_when_progressIsPreActivity() {
        //given
        LocalDate targetDate = LocalDate.of(2024, 2, 8);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);

        //when
        CertificationInformation information = certificationService.getCertificationInformation(instance,
                participant, targetDate);

        //then
        assertThat(information.pointPerPerson()).isEqualTo(instance.getPointPerPerson());
        assertThat(information.remainCount()).isEqualTo(information.totalAttempt());
        assertThat(information.totalAttempt()).isEqualTo(instance.getTotalAttempt());
        assertThat(information.currentAttempt()).isEqualTo(0);
        assertThat(information.successCount()).isEqualTo(0);
        assertThat(information.failureCount()).isEqualTo(0);
        assertThat(information.remainCount()).isEqualTo(instance.getTotalAttempt());
    }

    @Test
    @DisplayName("사용자가 참여한 챌린지가 진행 중이라면, 성공/실패/남은 일자의 값의 제대로 나와야 한다.")
    public void should_getInformation_when_progressIsActivity() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate targetDate = LocalDate.of(2024, 2, 8);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);

        //when
        instance.updateProgress(Progress.ACTIVITY);
        getSavedCertification(NOT_YET, startDate, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(4), participant);
        getSavedCertification(PASSED, startDate.plusDays(6), participant);
        CertificationInformation information = certificationService.getCertificationInformation(instance,
                participant, targetDate);

        //then
        assertThat(information.repository()).isEqualTo(participant.getRepositoryName());
        assertThat(information.totalAttempt()).isEqualTo(instance.getTotalAttempt());
        assertThat(information.currentAttempt()).isEqualTo(8);
        assertThat(information.pointPerPerson()).isEqualTo(instance.getPointPerPerson());
        assertThat(information.successCount()).isEqualTo(3);
        assertThat(information.failureCount()).isEqualTo(information.currentAttempt() - 3);
        assertThat(information.remainCount()).isEqualTo(instance.getTotalAttempt() - 8);
    }

    @Test
    @DisplayName("사용자가 참여한 챌린지가 완료이라면, 성공/실패/남은 일자의 값의 제대로 나와야 한다.")
    public void should_getInformation_when_progressIsDone() {
        //given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate targetDate = LocalDate.of(2024, 2, 8);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);

        //when
        instance.updateProgress(Progress.DONE);
        getSavedCertification(NOT_YET, startDate, participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, startDate.plusDays(4), participant);
        getSavedCertification(PASSED, startDate.plusDays(6), participant);
        CertificationInformation information = certificationService.getCertificationInformation(instance,
                participant, targetDate);

        //then
        assertThat(information.repository()).isEqualTo(participant.getRepositoryName());
        assertThat(information.totalAttempt()).isEqualTo(instance.getTotalAttempt());
        assertThat(information.currentAttempt()).isEqualTo(instance.getTotalAttempt());
        assertThat(information.pointPerPerson()).isEqualTo(instance.getPointPerPerson());
        assertThat(information.successCount()).isEqualTo(3);
        assertThat(information.failureCount()).isEqualTo(information.totalAttempt() - 3);
        assertThat(information.remainCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("챌린지에 참여한 모든 사용자들의 일주일 간 인증 현황을 받아올 수 있다. 단, 본인의 값을 제외한다.")
    public void should_getWeekCertification_aboutAllParticipants() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);
        LocalDate currentDate = LocalDate.of(2024, 3, 6);
        User user1 = getSavedUser(githubId, "nickname1");
        User user2 = getSavedUser(githubId, "nickname2");
        Instance instance = getSavedInstance();
        Participant participant1 = getSavedParticipant(user1, instance);
        Participant participant2 = getSavedParticipant(user2, instance);

        //when
        Slice<WeekResponse> certification = certificationService.getAllWeekCertification(
                user1.getId(), instance.getId(), currentDate, pageRequest);

        //then
        assertThat(certification.getContent().size()).isEqualTo(1);
        assertThat(certification.getContent().get(0).certifications().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("아직 인증을 하지 않았고, 패스 아이템이 있을 때 해당 일자의 인증을 패스할 수 있다.")
    public void should_passCertification_when_conditionIsValid() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        UserItem userItem = getSavedUserItem(user, ItemCategory.CERTIFICATION_PASSER, 1);
        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(currentDate)
                .build();

        //when
        instance.updateProgress(Progress.ACTIVITY);
        getSavedCertification(NOT_YET, currentDate, participant);
        getSavedCertification(CERTIFICATED, currentDate.plusDays(1), participant);
        getSavedCertification(CERTIFICATED, currentDate.plusDays(4), participant);
        getSavedCertification(CERTIFICATED, currentDate.plusDays(6), participant);

        ActivatedResponse activatedResponse = certificationService.passCertification(
                user.getId(),
                certificationRequest);

        //then
        assertThat(activatedResponse.instanceId()).isEqualTo(instance.getId());
        assertThat(activatedResponse.title()).isEqualTo(instance.getTitle());
        assertThat(activatedResponse.pointPerPerson()).isEqualTo(instance.getPointPerPerson());
        assertThat(activatedResponse.repository()).isEqualTo(participant.getRepositoryName());
        assertThat(activatedResponse.certificateStatus()).isEqualTo(PASSED.getTag());
        assertThat(activatedResponse.numOfPassItem()).isEqualTo(0);
        assertThat(activatedResponse.canUsePassItem()).isFalse();
        assertThat(activatedResponse.fileResponse()).isNotNull();
        assertThat(userItem.getCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("UserItem 정보가 DB에 존재하지 않을 때 인증 패스를 요청하면 예외가 발생해야 한다.")
    public void should_throwException_when_userItemInfoNotExist() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(currentDate)
                .build();

        //when
        getSavedCertification(NOT_YET, currentDate, participant);

        //then
        assertThatThrownBy(() -> certificationService.passCertification(user.getId(), certificationRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("UserItem 정보는 있으나 아이템의 개수가 0 이하일 때 인증 패스를 요청하면 예외가 발생해야 한다.")
    public void should_throwException_when_outOfStock() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        UserItem userItem = getSavedUserItem(user, ItemCategory.CERTIFICATION_PASSER, 0);
        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(currentDate)
                .build();

        instance.updateProgress(Progress.ACTIVITY);

        //when && then
        assertThatThrownBy(() -> certificationService.passCertification(user.getId(), certificationRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_ITEM_NOT_FOUND.getMessage());
    }

    @ParameterizedTest
    @DisplayName("패스 아이템은 있으나, 챌린지가 인증 필요 상태가 아니라면 예외가 발생해야 한다.")
    @EnumSource(mode = Mode.INCLUDE, names = {"CERTIFICATED", "PASSED"})
    public void should_throwException_when_challengeIsNotNOT_YET(CertificateStatus certificateStatus) {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        UserItem userItem = getSavedUserItem(user, ItemCategory.CERTIFICATION_PASSER, 1);
        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(currentDate)
                .build();

        //when
        instance.updateProgress(Progress.ACTIVITY);
        getSavedCertification(certificateStatus, currentDate, participant);

        //then
        assertThatThrownBy(() -> certificationService.passCertification(user.getId(), certificationRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.CAN_NOT_USE_PASS_ITEM.getMessage());
    }

    @Test
    @DisplayName("패스 아이템을 사용할 수 있고, 기존에 인증을 한 차례 시도했다면 PASSED로 데이터가 덮어진다.")
    public void should_overwriteData_when_certificatedBefore() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        UserItem userItem = getSavedUserItem(user, ItemCategory.CERTIFICATION_PASSER, 1);
        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(currentDate)
                .build();

        //when
        instance.updateProgress(Progress.ACTIVITY);
        getSavedCertification(NOT_YET, currentDate, participant);
        ActivatedResponse activatedResponse = certificationService.passCertification(user.getId(),
                certificationRequest);

        //then
        assertThat(activatedResponse.instanceId()).isEqualTo(instance.getId());
        assertThat(activatedResponse.title()).isEqualTo(instance.getTitle());
        assertThat(activatedResponse.pointPerPerson()).isEqualTo(instance.getPointPerPerson());
        assertThat(activatedResponse.repository()).isEqualTo(participant.getRepositoryName());
        assertThat(activatedResponse.certificateStatus()).isEqualTo(PASSED.getTag());
        assertThat(activatedResponse.numOfPassItem()).isEqualTo(0);
        assertThat(activatedResponse.canUsePassItem()).isFalse();
        assertThat(activatedResponse.fileResponse()).isNotNull();
    }

    @Test
    @DisplayName("패스 아이템을 가지고 있고, 이전에 인증을 한차례도 시도하지 않았을 때에도 아이템 사용이 가능하다")
    public void should_usePassItem_when_conditionIsValid() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser(githubId);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        UserItem userItem = getSavedUserItem(user, ItemCategory.CERTIFICATION_PASSER, 1);
        CertificationRequest certificationRequest = CertificationRequest.builder()
                .instanceId(instance.getId())
                .targetDate(currentDate)
                .build();

        //when
        instance.updateProgress(Progress.ACTIVITY);
        ActivatedResponse activatedResponse = certificationService.passCertification(user.getId(),
                certificationRequest);

        //then
        assertThat(activatedResponse.instanceId()).isEqualTo(instance.getId());
        assertThat(activatedResponse.title()).isEqualTo(instance.getTitle());
        assertThat(activatedResponse.pointPerPerson()).isEqualTo(instance.getPointPerPerson());
        assertThat(activatedResponse.repository()).isEqualTo(participant.getRepositoryName());
        assertThat(activatedResponse.certificateStatus()).isEqualTo(PASSED.getTag());
        assertThat(activatedResponse.numOfPassItem()).isEqualTo(0);
        assertThat(activatedResponse.canUsePassItem()).isFalse();
        assertThat(activatedResponse.fileResponse()).isNotNull();
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

    private User getSavedUser(String githubId, String nickname) {
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

    private Instance getSavedInstance() {
        return instanceRepository.save(
                Instance.builder()
                        .progress(Progress.PREACTIVITY)
                        .startedDate(LocalDateTime.of(2024, 2, 1, 0, 0))
                        .completedDate(LocalDateTime.of(2024, 3, 29, 0, 0))
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
        participant.updateRepository(targetRepo);

        return participant;
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

    private UserItem getSavedUserItem(User user, ItemCategory itemCategory, int count) {
        Item item = itemRepository.save(Item.builder()
                .itemCategory(itemCategory)
                .build());
        UserItem userItem = UserItem.createDefault(count, itemCategory);
        userItem.setItem(item);
        userItem.setUser(user);
        return userItemRepository.save(userItem);
    }
}