package com.genius.gitget.store.item.service;

import static com.genius.gitget.global.util.exception.ErrorCode.ORDERS_NOT_FOUND;

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
public class OrdersService {
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
                .orElseThrow(() -> new BusinessException(ORDERS_NOT_FOUND));
    }

    @Transactional
    public Orders findOrSave(User user, Item item) {
        return ordersRepository.findByOrderInfo(user.getId(), item.getId())
                .orElseGet(() -> ordersRepository.save(Orders.of(user, item)));
    }

    public List<Orders> findAllUsingFrames(Long userId) {
        List<Orders> frames = ordersRepository.findAllByCategory(userId, ItemCategory.PROFILE_FRAME);
        return frames.stream()
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
                    .identifier(null)
                    .build();
        }
        return usingFrames.get(0).getItem();
    }

    @Transactional
    public void useItem(Orders orders) {
        orders.useItem();
        if (!orders.hasItem()) {
            delete(orders);
        }
    }

    public int countNumOfItem(User user, Long itemId) {
        Optional<Orders> optionalUserItem = ordersRepository.findByOrderInfo(user.getId(), itemId);
        return optionalUserItem.map(Orders::getCount)
                .orElse(0);
    }

    public void validateUnmountCondition(Orders orders) {
        if (orders.getItem().getItemCategory() != ItemCategory.PROFILE_FRAME) {
            throw new BusinessException(ErrorCode.ITEM_NOT_FOUND);
        }
        if (orders.getEquipStatus() != EquipStatus.IN_USE) {
            throw new BusinessException(ErrorCode.IN_USE_FRAME_NOT_FOUND);
        }
    }
}
