package com.genius.gitget.challenge.item.service;

import com.genius.gitget.challenge.item.domain.EquipStatus;
import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import com.genius.gitget.challenge.item.dto.ItemResponse;
import com.genius.gitget.challenge.item.dto.ProfileResponse;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
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
        Long userPoint = persistUser.getPoint();

        if (item.getCost() > userPoint) {
            throw new BusinessException(ErrorCode.NOT_ENOUGH_POINT);
        }

        UserItem userItem = userItemProvider.findOptionalById(persistUser.getId(), itemId)
                .orElseGet(() -> createNew(persistUser, item));
        int numOfItem = userItem.purchase();
        persistUser.updatePoints((long) item.getCost() * -1);

        return getItemResponse(persistUser, item, numOfItem);
    }

    private UserItem createNew(User user, Item item) {
        UserItem userItem = UserItem.createDefault(item.getItemCategory());
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
