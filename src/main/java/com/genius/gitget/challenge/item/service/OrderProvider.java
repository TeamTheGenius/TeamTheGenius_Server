package com.genius.gitget.challenge.item.service;

import static com.genius.gitget.global.util.exception.ErrorCode.USER_ITEM_NOT_FOUND;

import com.genius.gitget.challenge.item.domain.EquipStatus;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.Order;
import com.genius.gitget.challenge.item.repository.OrderRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.exception.BusinessException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderProvider {
    private final OrderRepository orderRepository;


    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> findOptionalByOrderInfo(Long userId, Long itemId) {
        return orderRepository.findByOrderInfo(userId, itemId);
    }

    public Order findByOrderInfo(Long userId, Long itemId) {
        return orderRepository.findByOrderInfo(userId, itemId)
                .orElseThrow(() -> new BusinessException(USER_ITEM_NOT_FOUND));
    }

    public EquipStatus getEquipStatus(Long userId, Long itemId) {
        Optional<Order> optionalUserItem = orderRepository.findByOrderInfo(userId, itemId);
        if (optionalUserItem.isPresent()) {
            return optionalUserItem.get().getEquipStatus();
        }
        return EquipStatus.UNAVAILABLE;
    }

    public int countNumOfCategory(User user, ItemCategory itemCategory) {
        return orderRepository.findByCategory(user.getId(), itemCategory).size();
    }

    public int countNumOfItem(User user, Long itemId) {
        Optional<Order> optionalUserItem = orderRepository.findByOrderInfo(user.getId(), itemId);
        return optionalUserItem.map(Order::getCount)
                .orElse(0);
    }
}
