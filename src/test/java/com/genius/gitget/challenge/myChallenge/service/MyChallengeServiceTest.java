package com.genius.gitget.challenge.myChallenge.service;

import static com.genius.gitget.challenge.participant.domain.JoinResult.PROCESSING;
import static com.genius.gitget.challenge.participant.domain.JoinResult.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.PreActivityResponse;
import com.genius.gitget.challenge.participant.domain.JoinResult;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.domain.RewardStatus;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class MyChallengeServiceTest {
    @Autowired
    private MyChallengeService myChallengeService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private CertificationRepository certificationRepository;

    @Test
    @DisplayName("사용자가 참여한 챌린지들 중, 시작 전인 챌린지들을 받아올 수 있다.")
    public void should_getPreActivities_when_userJoinChallenges() {
        //given
        LocalDate targetDate = LocalDate.of(2024, 2, 14);
        User user = getSavedUser();
        Instance instance1 = getSavedInstance(Progress.PREACTIVITY);
        Instance instance2 = getSavedInstance(Progress.PREACTIVITY);
        Instance instance3 = getSavedInstance(Progress.PREACTIVITY);
        Participant participant1 = getSavedParticipant(user, instance1, PROCESSING);
        Participant participant2 = getSavedParticipant(user, instance2, PROCESSING);
        Participant participant3 = getSavedParticipant(user, instance3, PROCESSING);

        //when
        List<PreActivityResponse> instances = myChallengeService.getPreActivityInstances(user, targetDate);

        //then
        assertThat(instances.size()).isEqualTo(3);
        assertThat(instances.get(0).instanceId()).isEqualTo(instance1.getId());

        assertThat(instances.get(0).fileResponse()).isNotNull();
        assertThat(instances.get(1).instanceId()).isEqualTo(instance2.getId());
        assertThat(instances.get(1).fileResponse()).isNotNull();
        assertThat(instances.get(2).instanceId()).isEqualTo(instance3.getId());
        assertThat(instances.get(2).fileResponse()).isNotNull();
    }

    @Test
    @DisplayName("진행 중인 챌린지 목록 조회 시, 패스 아이템을 사용할 수 있는 조건이라면 아이템 사용 가능하다는 데이터를 반환한다.")
    public void should_getActivatedList_when_userJoinChallenges() {
        //given
        LocalDate targetDate = LocalDate.of(2024, 2, 14);
        User user = getSavedUser();
        Instance instance1 = getSavedInstance(Progress.ACTIVITY);
        Instance instance2 = getSavedInstance(Progress.ACTIVITY);
        Participant participant1 = getSavedParticipant(user, instance1, PROCESSING);
        Participant participant2 = getSavedParticipant(user, instance2, PROCESSING);

        Item item = itemRepository.findAllByCategory(ItemCategory.CERTIFICATION_PASSER).get(0);
        Orders orders = getSavedOrder(user, item, item.getItemCategory(), 3);

        //when
        getSavedCertification(CertificateStatus.NOT_YET, targetDate, participant2);
        List<ActivatedResponse> activatedResponses = myChallengeService.getActivatedInstances(user, targetDate);

        //then
        assertThat(activatedResponses.size()).isEqualTo(2);
        assertThat(activatedResponses.get(0).getNumOfPassItem()).isEqualTo(3);
        assertThat(activatedResponses.get(1).getNumOfPassItem()).isEqualTo(3);
    }

    @ParameterizedTest
    @DisplayName("진행 중인 챌린지 목록 조회 시, NOT_YET을 제외한 챌린지들은 아이템 사용 가능 여부가 false여야 한다.")
    @EnumSource(mode = Mode.INCLUDE, names = {"CERTIFICATED", "PASSED"})
    public void should_canUserPassItemIsFalse_when_AlreadyCertificated(CertificateStatus certificateStatus) {
        //given
        LocalDate targetDate = LocalDate.of(2024, 2, 14);
        User user = getSavedUser();
        Instance instance1 = getSavedInstance(Progress.ACTIVITY);
        Participant participant1 = getSavedParticipant(user, instance1, PROCESSING);

        Item item = getSavedItem(ItemCategory.CERTIFICATION_PASSER);
        getSavedOrder(user, item, item.getItemCategory(), 3);

        //when
        getSavedCertification(certificateStatus, targetDate, participant1);
        List<ActivatedResponse> instances = myChallengeService.getActivatedInstances(user, targetDate);

        //then
        assertThat(instances.size()).isEqualTo(1);
        assertThat(instances.get(0).getCertificateStatus()).isEqualTo(certificateStatus.getTag());
        assertThat(instances.get(0).getNumOfPassItem()).isEqualTo(0);
        assertThat(instances.get(0).getPointPerPerson()).isEqualTo(instance1.getPointPerPerson());
    }

    @Test
    @DisplayName("완료된 챌린지 목록 조회 시, 포인트를 아직 수령하지 않은 챌린지에 대해서는 보상 가능 정보를 전달해야 한다.")
    public void should_returnTrue_when_ableToReward() {
        //given
        LocalDateTime targetDate = LocalDateTime.of(2024, 2, 14, 0, 0);
        User user = getSavedUser();
        Instance instance = getSavedInstance(Progress.DONE, targetDate, targetDate.plusDays(1));
        Participant participant = getSavedParticipant(user, instance, SUCCESS);
        Item item = itemRepository.findAllByCategory(ItemCategory.POINT_MULTIPLIER).get(0);
        getSavedOrder(user, item, item.getItemCategory(), 3);

        //when
        getSavedCertification(CertificateStatus.PASSED, targetDate.toLocalDate(), participant);
        getSavedCertification(CertificateStatus.CERTIFICATED, targetDate.plusDays(1).toLocalDate(), participant);
        List<DoneResponse> doneResponses = myChallengeService.getDoneInstances(user, targetDate.toLocalDate());

        //then
        DoneResponse doneResponse = doneResponses.get(0);
        assertThat(doneResponses.size()).isEqualTo(1);
        assertThat(doneResponse.getTitle()).isEqualTo(instance.getTitle());
        assertThat(doneResponse.getInstanceId()).isEqualTo(instance.getId());
        assertThat(doneResponse.getRewardedPoints()).isZero();
        assertThat(doneResponse.getJoinResult()).isEqualTo(SUCCESS);
        assertThat(doneResponse.getFileResponse()).isNotNull();
        assertThat(doneResponse.isCanGetReward()).isTrue();
        assertThat(doneResponse.getNumOfPointItem()).isEqualTo(3);
    }

    @Test
    @DisplayName("완료된 챌린지 목록 조회 시, 포인트를 수령한 챌린지에 대해서는 포인트 수령 정보를 전달해야 한다.")
    public void should_returnRewardInfo_when_alreadyRewarded() {
        LocalDate targetDate = LocalDate.of(2024, 2, 14);
        User user = getSavedUser();
        Instance instance = getSavedInstance(Progress.DONE);
        getSavedParticipant(user, instance, SUCCESS);

        Item item = getSavedItem(ItemCategory.POINT_MULTIPLIER);
        getSavedOrder(user, item, item.getItemCategory(), 3);

        //when
        List<DoneResponse> doneResponses = myChallengeService.getDoneInstances(user, targetDate);

        //then
        assertThat(doneResponses.size()).isEqualTo(1);
        assertThat(doneResponses.get(0).isCanGetReward()).isTrue();
    }


    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("githubId")
                        .information("information")
                        .tags("BE,FE")
                        .build()
        );
    }

    private Instance getSavedInstance(Progress progress) {
        return instanceRepository.save(
                Instance.builder()
                        .progress(progress)
                        .pointPerPerson(100)
                        .title("title")
                        .startedDate(LocalDateTime.of(2024, 2, 1, 11, 3))
                        .completedDate(LocalDateTime.of(2024, 3, 29, 23, 59))
                        .build()
        );
    }

    private Instance getSavedInstance(Progress progress, LocalDateTime startedDate, LocalDateTime completedDate) {
        return instanceRepository.save(
                Instance.builder()
                        .progress(progress)
                        .pointPerPerson(100)
                        .title("title")
                        .startedDate(startedDate)
                        .completedDate(completedDate)
                        .build()
        );
    }

    private Participant getSavedParticipant(User user, Instance instance, JoinResult joinResult) {
        Participant participant = participantRepository.save(
                Participant.builder()
                        .joinResult(joinResult)
                        .joinStatus(JoinStatus.YES)
                        .rewardStatus(RewardStatus.NO)
                        .build()
        );
        participant.setUserAndInstance(user, instance);
        instance.updateParticipantCount(1);
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

    private Item getSavedItem(ItemCategory itemCategory) {
        return itemRepository.save(Item.builder()
                .itemCategory(itemCategory)
                .cost(100)
                .name(itemCategory.getName())
                .build());
    }

    private Orders getSavedOrder(User user, Item item, ItemCategory itemCategory, int count) {
        Orders orders = Orders.createDefault(count, itemCategory);
        orders.setItem(item);
        orders.setUser(user);
        return ordersRepository.save(orders);
    }
}