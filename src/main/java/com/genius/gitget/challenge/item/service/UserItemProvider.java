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

    public UserItem findByCategory(Long userId, ItemCategory itemCategory) {
        return userItemRepository.findByCategory(userId, itemCategory)
                .orElseThrow(() -> new BusinessException(USER_ITEM_NOT_FOUND));
    }

    public Optional<UserItem> findOptionalById(Long userId, Long itemId) {
        return userItemRepository.findByUserId(userId, itemId);
    }

    public EquipStatus getEquipStatus(Long userId, ItemCategory itemCategory) {
        Optional<UserItem> optionalUserItem = userItemRepository.findByCategory(userId, itemCategory);
        if (optionalUserItem.isPresent()) {
            return optionalUserItem.get().getEquipStatus();
        }
        return EquipStatus.UNAVAILABLE;
    }

    public int countNumOfItem(User user, ItemCategory itemCategory) {
        Optional<UserItem> optionalUserItem = userItemRepository.findByCategory(user.getId(), itemCategory);
        return optionalUserItem.map(UserItem::getCount)
                .orElse(0);
    }
}
