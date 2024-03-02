package com.genius.gitget.challenge.item.service;

import static com.genius.gitget.global.util.exception.ErrorCode.USER_ITEM_NOT_FOUND;

import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import com.genius.gitget.challenge.item.repository.UserItemRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserItemProvider {
    private final UserItemRepository userItemRepository;

    public UserItem findUserItemByUser(User user, ItemCategory itemCategory) {
        return userItemRepository.findUserItemByUser(user.getId(), itemCategory)
                .orElseThrow(() -> new BusinessException(USER_ITEM_NOT_FOUND));
    }
}
