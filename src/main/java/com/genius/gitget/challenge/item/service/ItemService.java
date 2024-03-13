package com.genius.gitget.challenge.item.service;

import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.service.CertificationService;
import com.genius.gitget.challenge.item.domain.EquipStatus;
import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
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
    private final UserItemProvider userItemProvider;

    //TODO: Service를 의존하는게 맘에 들지 않는다
    // provider만을 의존하게 할 순 없을까?
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
            int numOfItem = userItemProvider.countNumOfItem(user, item.getId());
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

        UserItem userItem = userItemProvider.findOptionalByInfo(persistUser.getId(), itemId)
                .orElseGet(() -> createNew(persistUser, item));
        int numOfItem = userItem.purchase();
        persistUser.updatePoints((long) item.getCost() * -1);

        return getItemResponse(persistUser, item, numOfItem);
    }

    @Transactional
    public ItemUseResponse useItem(User user, Long itemId, Long instanceId) {
        User persistUser = userService.findUserById(user.getId());
        Item item = itemProvider.findById(itemId);

        UserItem userItem = userItemProvider.findByInfo(user.getId(), itemId);
        if (!userItem.hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }

        switch (item.getItemCategory()) {
            case PROFILE_FRAME -> {
                validateFrameEquip(userItem);
                userItem.updateEquipStatus(EquipStatus.IN_USE);
                userItem.useItem();
                return new ItemUseResponse(0L, "", 0);
            }
            case CERTIFICATION_PASSER -> {
                ActivatedResponse activatedResponse = certificationService.passCertification(persistUser.getId(),
                        new CertificationRequest(instanceId, LocalDate.now()));
                activatedResponse.setItemId(itemId);
                userItem.useItem();
                return activatedResponse;
            }
            case POINT_MULTIPLIER -> {
                DoneResponse doneResponse = myChallengeService.getRewards(
                        new RewardRequest(persistUser, instanceId, LocalDate.now()), true
                );
                userItem.useItem();
                return doneResponse;
            }
        }
        throw new BusinessException(ErrorCode.USER_ITEM_NOT_FOUND);
    }

    private void validateFrameEquip(UserItem frameItem) {
        if (!frameItem.hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }
        if (frameItem.getEquipStatus() != EquipStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.INVALID_EQUIP_CONDITION);
        }
    }

    private void validateUserPoint(long userPoint, int itemCost) {
        if (userPoint < itemCost) {
            throw new BusinessException(ErrorCode.NOT_ENOUGH_POINT);
        }
    }

    private UserItem createNew(User user, Item item) {
        UserItem userItem = UserItem.createDefault(0, item.getItemCategory());
        userItem.setUser(user);
        userItem.setItem(item);
        return userItemProvider.save(userItem);
    }


    private ItemResponse getItemResponse(User user, Item item, int numOfItem) {
        if (item.getItemCategory() == ItemCategory.PROFILE_FRAME) {
            EquipStatus equipStatus = userItemProvider.getEquipStatus(user.getId(), item.getId());
            return ProfileResponse.create(item, numOfItem, equipStatus.getTag());
        }
        return ItemResponse.create(item, numOfItem);
    }
}
