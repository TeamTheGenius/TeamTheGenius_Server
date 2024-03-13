package com.genius.gitget.challenge.item.service;

import static com.genius.gitget.global.util.exception.ErrorCode.USER_ITEM_NOT_FOUND;

import com.genius.gitget.challenge.item.domain.EquipStatus;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import com.genius.gitget.challenge.item.repository.ItemRepository;
import com.genius.gitget.challenge.item.repository.UserItemRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.exception.BusinessException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserItemProvider {
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;


    public UserItem save(UserItem userItem) {
        return userItemRepository.save(userItem);
    }

    //TODO: 수정 필요
    public UserItem findByCategory(Long userId, ItemCategory itemCategory) {
        return userItemRepository.findByCategory(userId, itemCategory)
                .orElseThrow(() -> new BusinessException(USER_ITEM_NOT_FOUND));
    }

    public Optional<UserItem> findOptionalByInfo(Long userId, Long itemId) {
        return userItemRepository.findByUserId(userId, itemId);
    }

    public UserItem findByInfo(Long userId, Long itemId) {
        return userItemRepository.findByUserId(userId, itemId)
                .orElseThrow(() -> new BusinessException(USER_ITEM_NOT_FOUND));
    }

    public EquipStatus getEquipStatus(Long userId, Long itemId) {
        Optional<UserItem> optionalUserItem = userItemRepository.findByUserId(userId, itemId);
        if (optionalUserItem.isPresent()) {
            return optionalUserItem.get().getEquipStatus();
        }
        return EquipStatus.UNAVAILABLE;
    }

    public int countNumOfItem(User user, ItemCategory itemCategory) {
        //TODO: itemCategory에 해당하는 얘들을 모두 모아서 count해야 함 -> @Query부터 고쳐야 함
        Optional<UserItem> optionalUserItem = userItemRepository.findByCategory(user.getId(), itemCategory);
        return optionalUserItem.map(UserItem::getCount)
                .orElse(0);
    }

    public int countNumOfItem(User user, Long itemId) {
        Optional<UserItem> optionalUserItem = userItemRepository.findByUserId(user.getId(), itemId);
        return optionalUserItem.map(UserItem::getCount)
                .orElse(0);
    }
}
