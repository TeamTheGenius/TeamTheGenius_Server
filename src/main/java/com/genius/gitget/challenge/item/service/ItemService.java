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
            ItemResponse itemResponse = getItemResponse(user, item, itemCategory);
            itemResponses.add(itemResponse);
        }

        return itemResponses;
    }

    //TODO: 재구매가 불가능함 -> userItem을 찾는 과정에서 2개의 결과가 나온다고 함. 원래는 하나만 나와야 함
    @Transactional
    public ItemResponse orderItem(User user, Long itemId) {
        User persistUser = userService.findUserById(user.getId());
        Item item = itemProvider.findById(itemId);
        Long userPoint = persistUser.getPoint();

        if (item.getCost() > userPoint) {
            throw new BusinessException(ErrorCode.NOT_ENOUGH_POINT);
        }

        UserItem userItem = userItemProvider.findOptionalById(persistUser.getId(), itemId)
                .orElse(createNew(persistUser, item));

        userItem.purchase();
        return getItemResponse(persistUser, item, item.getItemCategory());
    }

    private UserItem createNew(User user, Item item) {
        UserItem userItem = UserItem.createDefault(item.getItemCategory());
        userItem.setUser(user);
        userItem.setItem(item);
        return userItemProvider.save(userItem);
    }


    private ItemResponse getItemResponse(User user, Item item, ItemCategory itemCategory) {
        if (itemCategory == ItemCategory.PROFILE_FRAME) {
            EquipStatus equipStatus = userItemProvider.getEquipStatus(user.getId(), ItemCategory.PROFILE_FRAME);
            return ProfileResponse.create(item, equipStatus.getTag());
        }
        int count = userItemProvider.countNumOfItem(user, itemCategory);
        return ItemResponse.create(item, count);
    }
}
