package com.genius.gitget.store.item.service;

import static com.genius.gitget.global.util.exception.ErrorCode.USER_ITEM_NOT_FOUND;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.EquipStatus;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.repository.OrdersRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrdersProvider {
    private final OrdersRepository ordersRepository;


    @Transactional
    public Orders save(Orders orders) {
        return ordersRepository.save(orders);
    }

    @Transactional
    public void delete(Orders orders) {
        ordersRepository.delete(orders);
    }

    public Optional<Orders> findOptionalByOrderInfo(Long userId, Long itemId) {
        return ordersRepository.findByOrderInfo(userId, itemId);
    }

    public Orders findByOrderInfo(Long userId, Long itemId) {
        return ordersRepository.findByOrderInfo(userId, itemId)
                .orElseThrow(() -> new BusinessException(USER_ITEM_NOT_FOUND));
    }


    public List<Orders> findAllByCategory(Long userId, ItemCategory itemCategory) {
        return ordersRepository.findAllByCategory(userId, itemCategory);
    }

    public List<Orders> findAllUsingFrames(Long userId) {
        return findAllByCategory(userId, ItemCategory.PROFILE_FRAME)
                .stream()
                .filter(frameOrder -> frameOrder.getEquipStatus() == EquipStatus.IN_USE)
                .toList();
    }

    public EquipStatus getEquipStatus(Long userId, Long itemId) {
        Optional<Orders> optionalUserItem = ordersRepository.findByOrderInfo(userId, itemId);
        if (optionalUserItem.isPresent()) {
            return optionalUserItem.get().getEquipStatus();
        }
        return EquipStatus.UNAVAILABLE;
    }

    public Item getUsingFrameItem(Long userId) {
        List<Orders> usingFrames = findAllUsingFrames(userId);
        if (usingFrames.size() > 1) {
            throw new BusinessException(ErrorCode.TOO_MANY_USING_FRAME);
        }

        if (usingFrames.isEmpty()) {
            return Item.builder()
                    .itemCategory(ItemCategory.PROFILE_FRAME)
                    .build();
        }
        return usingFrames.get(0).getItem();
    }

    public int countNumOfItem(User user, Long itemId) {
        Optional<Orders> optionalUserItem = ordersRepository.findByOrderInfo(user.getId(), itemId);
        return optionalUserItem.map(Orders::getCount)
                .orElse(0);
    }
}
