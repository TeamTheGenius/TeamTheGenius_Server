package com.genius.gitget.challenge.item.service;

import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.service.CertificationService;
import com.genius.gitget.challenge.item.domain.EquipStatus;
import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.Order;
import com.genius.gitget.challenge.item.dto.ItemResponse;
import com.genius.gitget.challenge.item.dto.ItemUseResponse;
import com.genius.gitget.challenge.item.dto.ProfileResponse;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.myChallenge.dto.RewardRequest;
import com.genius.gitget.challenge.myChallenge.service.MyChallengeService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
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
    private final OrderProvider orderProvider;

    //TODO: Service를 의존하는게 맘에 들지 않는다 provider만을 의존하게 할 순 없을까?
    private final CertificationService certificationService;
    private final MyChallengeService myChallengeService;

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
            int numOfItem = orderProvider.countNumOfItem(user, item.getId());
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

        Order order = orderProvider.findOptionalByOrderInfo(persistUser.getId(), itemId)
                .orElseGet(() -> createNew(persistUser, item));
        int numOfItem = order.purchase();
        persistUser.updatePoints((long) item.getCost() * -1);

        return getItemResponse(persistUser, item, numOfItem);
    }

    private void validateUserPoint(long userPoint, int itemCost) {
        if (userPoint < itemCost) {
            throw new BusinessException(ErrorCode.NOT_ENOUGH_POINT);
        }
    }

    private Order createNew(User user, Item item) {
        Order order = Order.createDefault(0, item.getItemCategory());
        order.setUser(user);
        order.setItem(item);
        return orderProvider.save(order);
    }

    @Transactional
    public ProfileResponse unmountFrame(User user, Long itemId) {
        Order order = orderProvider.findByOrderInfo(user.getId(), itemId);
        validateUnmountCondition(order);

        order.updateEquipStatus(EquipStatus.AVAILABLE);

        return ProfileResponse.create(
                order.getItem(), order.getCount(), order.getEquipStatus().getTag());
    }

    private void validateUnmountCondition(Order order) {
        if (order.getItem().getItemCategory() != ItemCategory.PROFILE_FRAME) {
            throw new BusinessException(ErrorCode.ITEM_NOT_FOUND);
        }
        if (order.getEquipStatus() != EquipStatus.IN_USE) {
            throw new BusinessException(ErrorCode.IN_USE_FRAME_NOT_FOUND);
        }
    }

    @Transactional
    public ItemUseResponse useItem(User user, Long itemId, Long instanceId, LocalDate currentDate) {
        Item item = itemProvider.findById(itemId);
        Order order = orderProvider.findByOrderInfo(user.getId(), itemId);

        if (!order.hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }

        switch (item.getItemCategory()) {
            case PROFILE_FRAME -> {
                return useProfileFrameItem(order);
            }
            case CERTIFICATION_PASSER -> {
                return usePasserItem(order, instanceId, currentDate);
            }
            case POINT_MULTIPLIER -> {
                return usePointMultiplierItem(order, instanceId, currentDate);
            }
        }
        throw new BusinessException(ErrorCode.USER_ITEM_NOT_FOUND);
    }

    private ItemUseResponse useProfileFrameItem(Order order) {
        validateFrameEquip(order);
        order.updateEquipStatus(EquipStatus.IN_USE);
        return new ItemUseResponse(0L, "", 0);
    }

    private void validateFrameEquip(Order order) {
        if (!order.hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }
        if (order.getEquipStatus() != EquipStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.INVALID_EQUIP_CONDITION);
        }
    }

    private ItemUseResponse usePasserItem(Order order, Long instanceId, LocalDate currentDate) {
        Long userId = order.getUser().getId();
        Long itemId = order.getItem().getId();
        ActivatedResponse activatedResponse = certificationService.passCertification(
                userId,
                new CertificationRequest(instanceId, currentDate));
        activatedResponse.setItemId(itemId);
        order.useItem();
        return activatedResponse;
    }

    private ItemUseResponse usePointMultiplierItem(Order order, Long instanceId, LocalDate currentDate) {
        User user = order.getUser();
        DoneResponse doneResponse = myChallengeService.getRewards(
                new RewardRequest(user, instanceId, currentDate), true
        );
        order.useItem();
        return doneResponse;
    }

    private ItemResponse getItemResponse(User user, Item item, int numOfItem) {
        if (item.getItemCategory() == ItemCategory.PROFILE_FRAME) {
            EquipStatus equipStatus = orderProvider.getEquipStatus(user.getId(), item.getId());
            return ProfileResponse.create(item, numOfItem, equipStatus.getTag());
        }
        return ItemResponse.create(item, numOfItem);
    }
}
