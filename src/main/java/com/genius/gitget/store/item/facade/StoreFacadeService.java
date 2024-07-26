package com.genius.gitget.store.item.facade;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;

import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.service.CertificationProvider;
import com.genius.gitget.challenge.certification.service.CertificationService;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.myChallenge.service.MyChallengeService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.EquipStatus;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.dto.OrderResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import com.genius.gitget.store.item.service.ItemService;
import com.genius.gitget.store.item.service.OrdersService;
import com.genius.gitget.store.payment.domain.Payment;
import com.genius.gitget.store.payment.repository.PaymentRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreFacadeService implements StoreFacade {
    private final ItemService itemService;
    private final OrdersService ordersService;

    private final UserService userService;

    //TODO: Facade 패턴 적용 시 CertificationService로 통일하기
    private final CertificationService certificationService;
    private final CertificationProvider certificationProvider;

    //TODO: MyChallengeService 말고 책임이 분명한 Service를 만들어서 적용하기
    private final MyChallengeService myChallengeService;

    private final PaymentRepository paymentRepository;


    @Override
    public List<ItemResponse> getItemsByCategory(User user, ItemCategory itemCategory) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        List<Item> items = itemService.findAllByCategory(itemCategory);
        for (Item item : items) {
            int numOfItem = ordersService.countNumOfItem(user, item.getId());
            ItemResponse itemResponse = getItemResponse(user, item, numOfItem);
            itemResponses.add(itemResponse);
        }

        return itemResponses;
    }

    private ItemResponse getItemResponse(User user, Item item, int numOfItem) {
        if (item.getItemCategory() == ItemCategory.PROFILE_FRAME) {
            EquipStatus equipStatus = ordersService.getEquipStatus(user.getId(), item.getId());
            return ProfileResponse.create(item, numOfItem, equipStatus.getTag());
        }
        return ItemResponse.create(item, numOfItem);
    }

    @Override
    public ItemResponse orderItem(User user, Long itemId) {
        User persistUser = userService.findUserById(user.getId());
        Item item = itemService.findById(itemId);

        persistUser.hasEnoughPoint(item.getCost());

        paymentRepository.save(Payment.create(user, item));

        Orders orders = ordersService.findOrSave(user, item);
        int numOfItem = orders.purchase();
        persistUser.updatePoints((long) item.getCost() * -1);

        return getItemResponse(persistUser, item, numOfItem);
    }

    @Override
    public OrderResponse useItem(User user, Long itemId, Long instanceId, LocalDate currentDate) {
        Item item = itemService.findById(itemId);
        Orders orders = ordersService.findByOrderInfo(user.getId(), itemId);

        if (!orders.hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }

        switch (item.getItemCategory()) {
            case PROFILE_FRAME -> {
                return useFrameItem(user.getId(), orders);
            }
            case CERTIFICATION_PASSER -> {
                return usePasserItem(orders, instanceId, currentDate);
            }
            case POINT_MULTIPLIER -> {
                return useMultiplierItem(orders, instanceId, currentDate);
            }
        }
        throw new BusinessException(ErrorCode.ORDERS_NOT_FOUND);
    }

    @Override
    public OrderResponse useFrameItem(Long userId, Orders orders) {
        validateFrameEquip(userId, orders);
        orders.updateEquipStatus(EquipStatus.IN_USE);

        return new OrderResponse(orders.getItem().getId());
    }

    private void validateFrameEquip(Long userId, Orders orders) {
        List<Orders> allUsingFrames = ordersService.findAllUsingFrames(userId);
        if (!allUsingFrames.isEmpty()) {
            throw new BusinessException(ErrorCode.TOO_MANY_USING_FRAME);
        }
        if (!orders.hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }
        if (orders.getEquipStatus() != EquipStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.INVALID_EQUIP_CONDITION);
        }
    }

    /**
     * 1. participant를 찾아서 해당 일자에 인증 여부 확인
     * 2. 인증이 가능한 조건이고 & 아직 인증 시도를 하지 않았거나, 인증이 안된 상태라면
     * 3. pass로 등록 (CertificationService로 처리되지  않을까)
     * 4. 적절한 응답 반환
     */
    @Override
    public OrderResponse usePasserItem(Orders orders, Long instanceId, LocalDate currentDate) {
        Long userId = orders.getUser().getId();
        Long itemId = orders.getItem().getId();
        ActivatedResponse activatedResponse = certificationService.passCertification(
                userId,
                new CertificationRequest(instanceId, currentDate));
        activatedResponse.setItemId(itemId);
        ordersService.useItem(orders);
        return activatedResponse;
    }

    /**
     * 일반 포인트 수령과 다른 부분: user의 포인트 업데이트 2배
     * 0. 수령받을 수 있는 조건인지 확인 -> 성공인지, 아직 보상을 안받았는지
     * 1. Participant를 찾은 후, 포인트 수령 처리
     * 2. instance에서 보상 포인트 확인
     * 3. user의 포인트 업데이트
     */
    @Override
    public OrderResponse useMultiplierItem(Orders orders, Long instanceId, LocalDate currentDate) {
        User user = orders.getUser();
        DoneResponse doneResponse = myChallengeService.getRewards(
                new RewardRequest(user, instanceId, currentDate), true
        );
        doneResponse.setItemId(orders.getItem().getId());
        ordersService.useItem(orders);
        return doneResponse;
    }

    private double getAchievementRate(Instance instance, Long participantId, LocalDate targetDate) {
        int totalAttempt = instance.getTotalAttempt();
        int successCount = certificationProvider.countByStatus(participantId, CERTIFICATED,
                targetDate);

        double successPercent = (double) successCount / (double) totalAttempt * 100;
        return Math.round(successPercent * 100 / 100.0);
    }

    @Override
    public List<ProfileResponse> unmountFrame(User user) {
        List<ProfileResponse> profileResponses = new ArrayList<>();
        List<Orders> frameOrders = ordersService.findAllUsingFrames(user.getId());

        for (Orders frameOrder : frameOrders) {
            validateUnmountCondition(frameOrder);
            frameOrder.updateEquipStatus(EquipStatus.AVAILABLE);
            profileResponses.add(ProfileResponse.createByEntity(frameOrder));
        }

        return profileResponses;
    }

    private void validateUnmountCondition(Orders orders) {
        if (orders.getItem().getItemCategory() != ItemCategory.PROFILE_FRAME) {
            throw new BusinessException(ErrorCode.ITEM_NOT_FOUND);
        }
        if (orders.getEquipStatus() != EquipStatus.IN_USE) {
            throw new BusinessException(ErrorCode.IN_USE_FRAME_NOT_FOUND);
        }
    }
}
