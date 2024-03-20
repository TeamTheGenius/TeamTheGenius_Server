package com.genius.gitget.challenge.item.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.PASSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
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
import com.genius.gitget.store.item.domain.EquipStatus;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.dto.ItemUseResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.store.item.service.ItemService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private CertificationRepository certificationRepository;

    @Test
    @DisplayName("데이터베이스에 저장되어 있는 모든 아이템 정보들을 받아올 수 있다.")
    public void should_getAllItems_when_itemsSaved() {
        //given
        User user = getSavedUser();

        //when
        List<ItemResponse> items = itemService.getAllItems(user);

        //then
        assertThat(items.size()).isEqualTo(4);
    }

    @ParameterizedTest
    @DisplayName("카테고리에 해당하는 아이템들을 받아올 수 있다.")
    @EnumSource(ItemCategory.class)
    public void should_getItems_when_passCategory(ItemCategory itemCategory) {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(itemCategory);
        Orders orders = getSavedOrder(user, item, itemCategory, 1);

        //when
        List<ItemResponse> itemResponses = itemService.getItemsByCategory(user, itemCategory);

        //then
        for (ItemResponse itemResponse : itemResponses) {
            assertThat(itemResponse.getName()).contains(itemCategory.getName());
        }
    }

    @ParameterizedTest
    @DisplayName("사용자의 포인트가 충분할 때, itemId(PK)를 전달하여 아이템을 구매할 수 있다.")
    @EnumSource(mode = Mode.EXCLUDE, names = {"PROFILE_FRAME"})
    public void should_purchaseItem_when_passPK(ItemCategory itemCategory) {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(itemCategory);

        user.updatePoints(1000L);

        //when
        ItemResponse itemResponse = itemService.orderItem(user, item.getId());

        //then
        assertThat(itemResponse.getItemId()).isEqualTo(item.getId());
        assertThat(itemResponse.getName()).isEqualTo(item.getName());
        assertThat(itemResponse.getCost()).isEqualTo(item.getCost());
        assertThat(itemResponse.getCount()).isEqualTo(1);
    }

    @ParameterizedTest
    @DisplayName("사용자의 포인트가 충분하지 않을 때, 아이템 구매를 시도하면 예외가 발생해야 한다.")
    @EnumSource(ItemCategory.class)
    public void should_throwException_when_pointNotEnough(ItemCategory itemCategory) {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(itemCategory);

        //when & then
        assertThatThrownBy(() -> itemService.orderItem(user, item.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NOT_ENOUGH_POINT.getMessage());
    }

    @Test
    @DisplayName("UserItem 정보는 있으나 아이템의 개수가 0 이하일 때 인증 패스를 요청하면 예외가 발생해야 한다.")
    public void should_throwException_when_outOfStock() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        Orders orders = getSavedOrder(user, item, ItemCategory.CERTIFICATION_PASSER, 0);

        instance.updateProgress(Progress.ACTIVITY);

        //when && then
        assertThatThrownBy(() -> itemService.useItem(user, item.getId(), instance.getId(), currentDate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.HAS_NO_ITEM.getMessage());
    }

    @Test
    @DisplayName("UserItem 정보가 DB에 존재하지 않을 때 인증 패스를 요청하면 예외가 발생해야 한다.")
    public void should_throwException_when_userItemInfoNotExist() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        Item item = getSavedItem(ItemCategory.CERTIFICATION_PASSER);

        //when
        getSavedCertification(NOT_YET, currentDate, participant);

        //then
        assertThatThrownBy(() -> itemService.useItem(user, item.getId(), instance.getId(), currentDate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("프로필 프레임을 구매했는데 장착하지 않은 경우에는 프레임을 사용할 수 있다.")
    public void should_useFrameItem_when_availableToEquip() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        Orders orders = getSavedOrder(user, item, item.getItemCategory(), 1);

        //when
        orders.updateEquipStatus(EquipStatus.AVAILABLE);
        ItemUseResponse itemUseResponse = itemService.useItem(user, item.getId(), instance.getId(), currentDate);

        //then
        assertThat(orders.getEquipStatus()).isEqualTo(EquipStatus.IN_USE);
    }

    @Test
    @DisplayName("프로필 프레임을 재구매시도할 경우 예외가 발생해야 한다.")
    public void should_throwException_when_tryOrderFrameAgain() {
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);

        user.updatePoints(1000L);

        // when
        itemService.orderItem(user, item.getId());

        //when & then
        assertThatThrownBy(() -> itemService.orderItem(user, item.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ALREADY_PURCHASED.getMessage());
    }

    @Test
    @DisplayName("프로필 프레임을 사용하려 할 때, 해당 프레임에 대한 수량이 0이라면 예외가 발생해야 한다.")
    public void should_throwException_when_dontHaveItem() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        getSavedOrder(user, item, ItemCategory.PROFILE_FRAME, 0);

        //when && then
        assertThatThrownBy(() -> itemService.useItem(user, item.getId(), 0L, LocalDate.now()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.HAS_NO_ITEM.getMessage());
    }

    @ParameterizedTest
    @DisplayName("프로필 프레임을 사용하려 할 때, 프레임의 장착 상태가 AVAILABLE이 아니라면 예외가 발생해야 한다.")
    @EnumSource(mode = Mode.INCLUDE, names = {"IN_USE", "UNAVAILABLE"})
    public void should_throwException_when_notAvailable(EquipStatus equipStatus) {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        Orders orders = getSavedOrder(user, item, ItemCategory.PROFILE_FRAME, 3);

        orders.updateEquipStatus(equipStatus);

        //when && then
        assertThatThrownBy(() -> itemService.useItem(user, item.getId(), 0L, LocalDate.now()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_EQUIP_CONDITION.getMessage());
    }

    @Test
    @DisplayName("인증 패스 아이템을 보유하고 있을 떄, 인증을 시도했으나 실패했을 때 아이템을 사용할 수 있다.")
    public void should_usePasserItem_when_ableToUsePassItem() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        Item item = getSavedItem(ItemCategory.CERTIFICATION_PASSER);
        Orders orders = getSavedOrder(user, item, item.getItemCategory(), 1);

        //when
        instance.updateProgress(Progress.ACTIVITY);
        Certification certification = getSavedCertification(NOT_YET, currentDate, participant);
        ItemUseResponse itemUseResponse = itemService.useItem(user, item.getId(), instance.getId(), currentDate);

        //then
        assertThat(itemUseResponse.getInstanceId()).isEqualTo(instance.getId());
        assertThat(itemUseResponse.getTitle()).isEqualTo(instance.getTitle());
        assertThat(itemUseResponse.getPointPerPerson()).isEqualTo(instance.getPointPerPerson());
        assertThat(orders.getCount()).isEqualTo(0);
        assertThat(certification.getCertificationStatus()).isEqualTo(PASSED);
    }

    @Test
    @DisplayName("인증 패스 아이템을 보유하고 있고, 인증을 아직 시도하지 않았을 때 아이템을 사용할 수 있다.")
    public void should_usePasserItem_when_useItemNotYet() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        Item item = getSavedItem(ItemCategory.CERTIFICATION_PASSER);
        Orders orders = getSavedOrder(user, item, item.getItemCategory(), 1);

        //when
        instance.updateProgress(Progress.ACTIVITY);

        //then
        ItemUseResponse itemUseResponse = itemService.useItem(user, item.getId(), instance.getId(), currentDate);

        //then
        Optional<Certification> certification = certificationRepository.findByDate(currentDate, participant.getId());
        assertThat(itemUseResponse.getInstanceId()).isEqualTo(instance.getId());
        assertThat(itemUseResponse.getTitle()).isEqualTo(instance.getTitle());
        assertThat(itemUseResponse.getPointPerPerson()).isEqualTo(instance.getPointPerPerson());
        assertThat(orders.getCount()).isEqualTo(0);
        assertThat(certification).isPresent();
        assertThat(certification.get().getCertificationStatus()).isEqualTo(PASSED);
    }

    @Test
    @DisplayName("인증 패스 아이템을 보유하고 있으나, 인스턴스의 상태가 ACTIVITY가 아니라면 사용 시도 시 예외가 발생해야 한다.")
    public void should_throwException_when_ProgressIsNotActivity() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        Item item = getSavedItem(ItemCategory.CERTIFICATION_PASSER);
        Orders orders = getSavedOrder(user, item, item.getItemCategory(), 1);

        //when & then
        assertThatThrownBy(() -> itemService.useItem(user, item.getId(), instance.getId(), currentDate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NOT_ACTIVITY_INSTANCE.getMessage());
    }

    @ParameterizedTest
    @DisplayName("인증 패스 아이템을 보유하고 있으나, 인증 상태가 PASSED 혹은 CERTIFICATED라면 예외가 발생해야 한다.")
    @EnumSource(mode = Mode.INCLUDE, names = {"CERTIFICATED", "PASSED"})
    public void should_throwException_when_notNOT_YET(CertificateStatus certificateStatus) {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser();
        Instance instance = getSavedInstance();
        Participant participant = getSavedParticipant(user, instance);
        Item item = getSavedItem(ItemCategory.CERTIFICATION_PASSER);
        getSavedOrder(user, item, item.getItemCategory(), 1);

        //when
        instance.updateProgress(Progress.ACTIVITY);
        getSavedCertification(certificateStatus, currentDate, participant);

        //then
        assertThatThrownBy(() -> itemService.useItem(user, item.getId(), instance.getId(), currentDate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.CAN_NOT_USE_PASS_ITEM.getMessage());
    }

    @Test
    @DisplayName("인스턴스의 상태가 DONE이고 2배 획득 아이템을 보유하고 있을 때, 포인트 보상을 2배로 받을 수 있다.")
    public void should_getRewardTwice_when_conditionMatches() {
        //given
        LocalDate currentDate = LocalDate.of(2024, 3, 1);
        User user = getSavedUser();
        Instance instance = getSavedInstance(currentDate, currentDate.plusDays(1));
        Participant participant = getSavedParticipant(user, instance);
        Item item = getSavedItem(ItemCategory.POINT_MULTIPLIER);
        Orders orders = getSavedOrder(user, item, item.getItemCategory(), 1);

        //when
        Long previousPoint = user.getPoint();
        instance.updateProgress(Progress.DONE);
        participant.updateJoinResult(JoinResult.SUCCESS);
        getSavedCertification(CERTIFICATED, currentDate, participant);
        getSavedCertification(CERTIFICATED, currentDate.plusDays(1), participant);
        itemService.useItem(user, item.getId(), instance.getId(), currentDate.plusDays(1));
        Long afterRewards = user.getPoint();

        //then
        assertThat(afterRewards - previousPoint).isEqualTo(instance.getPointPerPerson() * 2L);
        assertThat(orders.getCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자가 특정 프로필 프레임을 장착하고 있을 떄, 장착 해제할 수 있다.")
    public void should_unmountFrame_when_mountAlready() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        getSavedOrder(user, item, ItemCategory.PROFILE_FRAME, 1);

        //when
        itemService.useItem(user, item.getId(), 0L, LocalDate.now());
        ProfileResponse profileResponse = itemService.unmountFrame(user).get(0);

        //then
        assertThat(profileResponse.getItemId()).isEqualTo(item.getId());
        assertThat(profileResponse.getCost()).isEqualTo(item.getCost());
        assertThat(profileResponse.getItemCategory()).isEqualTo(ItemCategory.PROFILE_FRAME);
        assertThat(profileResponse.getEquipStatus()).isEqualTo(EquipStatus.AVAILABLE.getTag());
    }

    @ParameterizedTest
    @DisplayName("사용자가 아이템 장착 해제를 요청했을 때, 프로필 프레임이 아니라면 응답 데이터의 크기가 0이다.")
    @EnumSource(mode = Mode.EXCLUDE, names = {"PROFILE_FRAME"})
    public void should_throwException_when_categoryIsNotFrame(ItemCategory itemCategory) {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(itemCategory);
        Orders orders = getSavedOrder(user, item, item.getItemCategory(), 1);

        //when
        List<ProfileResponse> profileResponses = itemService.unmountFrame(user);

        //when & then
        assertThat(profileResponses.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자가 아이템 장착 해제를 요청했을 때, 사용 상태가 IN_USE가 아니라면 반환데이터의 크기가 0이다.")
    public void should_throwException_when_equipStatusIsNotIS_USE() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        getSavedOrder(user, item, item.getItemCategory(), 1);

        // when
        List<ProfileResponse> profileResponses = itemService.unmountFrame(user);

        //when & then
        assertThat(profileResponses.size()).isEqualTo(0);
    }


    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("identifier")
                        .build()
        );
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

    private Instance getSavedInstance() {
        return instanceRepository.save(
                Instance.builder()
                        .progress(Progress.PREACTIVITY)
                        .startedDate(LocalDateTime.of(2024, 2, 1, 0, 0))
                        .completedDate(LocalDateTime.of(2024, 3, 29, 0, 0))
                        .build()
        );
    }

    private Instance getSavedInstance(LocalDate startDate, LocalDate completeDate) {
        return instanceRepository.save(
                Instance.builder()
                        .progress(Progress.PREACTIVITY)
                        .startedDate(startDate.atTime(0, 0))
                        .completedDate(completeDate.atTime(0, 0))
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
        participant.updateRepository("targetRepo");

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
}