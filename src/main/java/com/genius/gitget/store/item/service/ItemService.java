package com.genius.gitget.store.item.service;

import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.service.CertificationService;
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
import com.genius.gitget.store.item.dto.ItemUseResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import com.genius.gitget.store.payment.domain.OrderType;
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
public class ItemService {
    private final UserService userService;
    private final ItemProvider itemProvider;
    private final OrdersProvider ordersProvider;

    //TODO: Service를 의존하는게 맘에 들지 않는다 provider만을 의존하게 할 순 없을까?
    private final CertificationService certificationService;
    private final MyChallengeService myChallengeService;

    private final PaymentRepository paymentRepository;

    public List<ItemResponse> getAllItems(User user) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        for (ItemCategory itemCategory : ItemCategory.values()) {
            itemResponses.addAll(getItemsByCategory(user, itemCategory));
        }
        return itemResponses;
    }

    public List<ItemResponse> getItemsByCategory(User user, ItemCategory itemCategory) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        List<Item> items = itemProvider.findAllByCategory(itemCategory);

        for (Item item : items) {
            int numOfItem = ordersProvider.countNumOfItem(user, item.getId());
            ItemResponse itemResponse = getItemResponse(user, item, numOfItem);
            itemResponses.add(itemResponse);
        }

        return itemResponses;
    }

    @Transactional
    public ItemResponse orderItem(User user, Long itemId) {
        User persistUser = userService.findUserById(user.getId());
        Item item = itemProvider.findById(itemId);

        validateUserPoint(persistUser.getPoint(), item.getCost());

        paymentRepository.save(getPayment(user, item));

        Orders orders = ordersProvider.findOptionalByOrderInfo(persistUser.getId(), itemId)
                .orElseGet(() -> createNew(persistUser, item));
        int numOfItem = orders.purchase();
        persistUser.updatePoints((long) item.getCost() * -1);

        return getItemResponse(persistUser, item, numOfItem);
    }

    private void validateUserPoint(long userPoint, int itemCost) {
        if (userPoint < itemCost) {
            throw new BusinessException(ErrorCode.NOT_ENOUGH_POINT);
        }
    }

    private Payment getPayment(User user, Item item) {
        return Payment.builder()
                .user(user)
                .orderType(OrderType.ITEM)
                .isSuccess(true)
                .pointAmount(Long.parseLong(String.valueOf(item.getCost())))
                .orderName(item.getName())
                .build();
    }

    private Orders createNew(User user, Item item) {
        Orders orders = Orders.createDefault(0, item.getItemCategory());
        orders.setUser(user);
        orders.setItem(item);
        return ordersProvider.save(orders);
    }

    @Transactional
    public List<ProfileResponse> unmountFrame(User user) {
        List<ProfileResponse> profileResponses = new ArrayList<>();
        List<Orders> frameOrders = ordersProvider.findAllUsingFrames(user.getId());

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

    @Transactional
    public ItemUseResponse useItem(User user, Long itemId, Long instanceId, LocalDate currentDate) {
        Item item = itemProvider.findById(itemId);
        Orders orders = ordersProvider.findByOrderInfo(user.getId(), itemId);

        if (!orders.hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }

        switch (item.getItemCategory()) {
            case PROFILE_FRAME -> {
                return useProfileFrameItem(user.getId(), orders);
            }
            case CERTIFICATION_PASSER -> {
                return usePasserItem(orders, instanceId, currentDate);
            }
            case POINT_MULTIPLIER -> {
                return usePointMultiplierItem(orders, instanceId, currentDate);
            }
        }
        throw new BusinessException(ErrorCode.USER_ITEM_NOT_FOUND);
    }

    private ItemUseResponse useProfileFrameItem(Long userId, Orders orders) {
        validateFrameEquip(userId, orders);
        orders.updateEquipStatus(EquipStatus.IN_USE);

        Item item = orders.getItem();
        return new ItemUseResponse(item.getId());
    }

    private void validateFrameEquip(Long userId, Orders orders) {
        List<Orders> allUsingFrames = ordersProvider.findAllUsingFrames(userId);
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

    private ItemUseResponse usePasserItem(Orders orders, Long instanceId, LocalDate currentDate) {
        Long userId = orders.getUser().getId();
        Long itemId = orders.getItem().getId();
        ActivatedResponse activatedResponse = certificationService.passCertification(
                userId,
                new CertificationRequest(instanceId, currentDate));
        activatedResponse.setItemId(itemId);
        useItem(orders);
        return activatedResponse;
    }

    private ItemUseResponse usePointMultiplierItem(Orders orders, Long instanceId, LocalDate currentDate) {
        User user = orders.getUser();
        DoneResponse doneResponse = myChallengeService.getRewards(
                new RewardRequest(user, instanceId, currentDate), true
        );
        doneResponse.setItemId(orders.getItem().getId());
        useItem(orders);
        return doneResponse;
    }

    private void useItem(Orders orders) {
        orders.useItem();
        if (!orders.hasItem()) {
            ordersProvider.delete(orders);
        }
    }

    private ItemResponse getItemResponse(User user, Item item, int numOfItem) {
        if (item.getItemCategory() == ItemCategory.PROFILE_FRAME) {
            EquipStatus equipStatus = ordersProvider.getEquipStatus(user.getId(), item.getId());
            return ProfileResponse.create(item, numOfItem, equipStatus.getTag());
        }
        return ItemResponse.create(item, numOfItem);
    }
}
