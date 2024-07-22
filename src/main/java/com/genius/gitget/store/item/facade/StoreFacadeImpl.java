package com.genius.gitget.store.item.facade;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.EquipStatus;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.dto.ItemUseResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import com.genius.gitget.store.item.service.ItemService;
import com.genius.gitget.store.item.service.OrdersService;
import com.genius.gitget.store.payment.domain.Payment;
import com.genius.gitget.store.payment.repository.PaymentRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

//@Service
//@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreFacadeImpl implements StoreFacade {
    private final UserService userService;
    private final ItemService itemService;
    private final OrdersService ordersService;

    private final PaymentRepository paymentRepository;


    @Override
    public List<ItemResponse> getItemsByCategory(User user, ItemCategory itemCategory) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        List<Item> items = itemService.findAllByCategory(itemCategory);
        for (Item item : items) {
            int numOfItem = ordersService.countNumOfItem(user, item.getId());
            ItemResponse itemResponse = getItemResponse(user, item, numOfItem);
            itemResponses.add(itemResponse);
        }

        return itemResponses;
    }

    private ItemResponse getItemResponse(User user, Item item, int numOfItem) {
        if (item.getItemCategory() == ItemCategory.PROFILE_FRAME) {
            EquipStatus equipStatus = ordersService.getEquipStatus(user.getId(), item.getId());
            return ProfileResponse.create(item, numOfItem, equipStatus.getTag());
        }
        return ItemResponse.create(item, numOfItem);
    }

    @Override
    public ItemResponse orderItem(User user, Long itemId) {
        User persistUser = userService.findUserById(user.getId());
        Item item = itemService.findById(itemId);

        persistUser.hasEnoughPoint(item.getCost());

        paymentRepository.save(Payment.create(user, item));

        Orders orders = ordersService.findOrSave(user, item);
        int numOfItem = orders.purchase();
        persistUser.updatePoints((long) item.getCost() * -1);

        return getItemResponse(persistUser, item, numOfItem);
    }

    @Override
    public ItemUseResponse useItem(User user, Long itemId, Long instanceId, LocalDate currentDate) {
        return null;
    }

    @Override
    public ItemUseResponse useFrameItem(Long userId, Orders orders) {
        validateFrameEquip(userId, orders);
        orders.updateEquipStatus(EquipStatus.IN_USE);

        return new ItemUseResponse(orders.getItem().getId());
    }

    private void validateFrameEquip(Long userId, Orders orders) {
        List<Orders> allUsingFrames = ordersService.findAllUsingFrames(userId);
        if (!allUsingFrames.isEmpty()) {
            throw new BusinessException(ErrorCode.TOO_MANY_USING_FRAME);
        }
        if (!orders.hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }
        if (orders.getEquipStatus() != EquipStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.INVALID_EQUIP_CONDITION);
        }
    }

    /**
     * 1. participant를 찾아서 해당 일자에 인증 여부 확인
     * 2. 인증이 가능한 조건이고 & 아직 인증 시도를 하지 않았거나, 인증이 안된 상태라면
     * 3. pass로 등록 (CertificationService로 처리되지  않을까)
     * 4. 적절한 응답 반환
     */
    @Override
    public ItemUseResponse usePasserItem(Orders orders, Long instanceId, LocalDate currentDate) {
        return null;
    }

    /**
     * 일반 포인트 수령과 다른 부분: user의 포인트 업데이트 2배 (이런 부분이 facade로 되어야)
     * 0. 수령받을 수 있는 조건인지 확인 -> 성공인지, 아직 보상을 안받았는지
     * 1. Participant를 찾은 후, 포인트 수령 처리
     * 2. instance에서 보상 포인트 확인
     * 3. user의 포인트 업데이트
     */
    @Override
    public ItemUseResponse useMultiplierItem(Orders orders, Long instanceId, LocalDate currentDate) {
        return null;
    }

    /**
     *
     */
    @Override
    public List<ProfileResponse> unmountFrame(User user) {
        return null;
    }
}
