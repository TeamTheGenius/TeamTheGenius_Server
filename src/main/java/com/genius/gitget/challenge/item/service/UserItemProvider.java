package com.genius.gitget.challenge.item.service;

import static com.genius.gitget.global.util.exception.ErrorCode.USER_ITEM_NOT_FOUND;

import com.genius.gitget.challenge.item.domain.EquipStatus;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
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
    private final UserItemRepository userItemRepository;


    public UserItem save(UserItem userItem) {
        return userItemRepository.save(userItem);
    }

    public Optional<UserItem> findOptionalByOrderInfo(Long userId, Long itemId) {
        return userItemRepository.findByOrderInfo(userId, itemId);
    }

    public UserItem findByOrderInfo(Long userId, Long itemId) {
        return userItemRepository.findByOrderInfo(userId, itemId)
                .orElseThrow(() -> new BusinessException(USER_ITEM_NOT_FOUND));
    }

    public EquipStatus getEquipStatus(Long userId, Long itemId) {
        Optional<UserItem> optionalUserItem = userItemRepository.findByOrderInfo(userId, itemId);
        if (optionalUserItem.isPresent()) {
            return optionalUserItem.get().getEquipStatus();
        }
        return EquipStatus.UNAVAILABLE;
    }

    public int countNumOfItem(User user, ItemCategory itemCategory) {
        return userItemRepository.findByCategory(user.getId(), itemCategory).size();
    }

    public int countNumOfItem(User user, Long itemId) {
        Optional<UserItem> optionalUserItem = userItemRepository.findByOrderInfo(user.getId(), itemId);
        return optionalUserItem.map(UserItem::getCount)
                .orElse(0);
    }
}
