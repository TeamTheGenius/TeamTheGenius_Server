package com.genius.gitget.challenge.item.service;

import static com.genius.gitget.global.util.exception.ErrorCode.USER_ITEM_NOT_FOUND;

import com.genius.gitget.challenge.item.domain.EquipStatus;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.Orders;
import com.genius.gitget.challenge.item.repository.OrdersRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.exception.BusinessException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrdersProvider {
    private final OrdersRepository ordersRepository;


    public Orders save(Orders orders) {
        return ordersRepository.save(orders);
    }

    public Optional<Orders> findOptionalByOrderInfo(Long userId, Long itemId) {
        return ordersRepository.findByOrderInfo(userId, itemId);
    }

    public Orders findByOrderInfo(Long userId, Long itemId) {
        return ordersRepository.findByOrderInfo(userId, itemId)
                .orElseThrow(() -> new BusinessException(USER_ITEM_NOT_FOUND));
    }

    public EquipStatus getEquipStatus(Long userId, Long itemId) {
        Optional<Orders> optionalUserItem = ordersRepository.findByOrderInfo(userId, itemId);
        if (optionalUserItem.isPresent()) {
            return optionalUserItem.get().getEquipStatus();
        }
        return EquipStatus.UNAVAILABLE;
    }

    public int countNumOfCategory(User user, ItemCategory itemCategory) {
        return ordersRepository.findByCategory(user.getId(), itemCategory).size();
    }

    public int countNumOfItem(User user, Long itemId) {
        Optional<Orders> optionalUserItem = ordersRepository.findByOrderInfo(user.getId(), itemId);
        return optionalUserItem.map(Orders::getCount)
                .orElse(0);
    }
}
