package com.genius.gitget.store.item.facade;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;

import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.service.CertificationService;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.challenge.myChallenge.dto.DoneResponse;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.EquipStatus;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.dto.OrderResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import com.genius.gitget.store.item.service.ItemService;
import com.genius.gitget.store.item.service.OrdersService;
import com.genius.gitget.store.payment.domain.Payment;
import com.genius.gitget.store.payment.repository.PaymentRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreFacadeService implements StoreFacade {
    private final ItemService itemService;
    private final OrdersService ordersService;

    private final UserService userService;
    private final InstanceService instanceService;
    private final ParticipantService participantService;

    private final CertificationService certificationService;
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
    @Transactional
    public ItemResponse orderItem(User user, int identifier) {
        User persistUser = userService.findUserById(user.getId());
        Item item = itemService.findByIdentifier(identifier);

        persistUser.hasEnoughPoint(item.getCost());

        paymentRepository.save(Payment.create(persistUser, item));

        Orders orders = ordersService.findOrSave(persistUser, item);
        int numOfItem = orders.purchase();
        persistUser.updatePoints((long) item.getCost() * -1);

        return getItemResponse(persistUser, item, numOfItem);
    }

    @Override
    @Transactional
    public OrderResponse useItem(User user, int identifier, Long instanceId, LocalDate currentDate) {
        Item item = itemService.findByIdentifier(identifier);
        Orders orders = ordersService.findByOrderInfo(user.getId(), item.getId());

        if (!orders.hasItem()) {
            throw new BusinessException(ErrorCode.HAS_NO_ITEM);
        }

        switch (item.getItemCategory()) {
            case PROFILE_FRAME -> {
                return useFrameItem(user.getId(), orders);
            }
            case CERTIFICATION_PASSER -> {
                return usePasserItem(orders, instanceId, currentDate);
            }
            case POINT_MULTIPLIER -> {
                return useMultiplierItem(orders, instanceId, currentDate);
            }
        }
        throw new BusinessException(ErrorCode.ORDERS_NOT_FOUND);
    }

    @Override
    public OrderResponse useFrameItem(Long userId, Orders orders) {
        validateFrameEquip(userId, orders);
        orders.updateEquipStatus(EquipStatus.IN_USE);

        return new OrderResponse(orders.getItem().getId());
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

    @Override
    public OrderResponse usePasserItem(Orders orders, Long instanceId, LocalDate currentDate) {
        Long userId = orders.getUser().getId();
        Long itemId = orders.getItem().getId();

        Instance instance = instanceService.findInstanceById(instanceId);
        Participant participant = participantService.findByJoinInfo(userId, instanceId);

        Certification certification = certificationService.findOrSave(participant, NOT_YET, currentDate);

        instance.validateCertificateCondition(currentDate);
        certification.validatePassCondition();

        certification.updateToPass(currentDate);

        ordersService.useItem(orders);
        return OrderResponse.of(itemId);
    }

    @Override
    public OrderResponse useMultiplierItem(Orders orders, Long instanceId, LocalDate currentDate) {
        User user = orders.getUser();
        Instance instance = instanceService.findInstanceById(instanceId);
        Participant participant = participantService.findByJoinInfo(user.getId(), instanceId);

        int rewardPoints = instance.getPointPerPerson() * 2;
        participantService.getRewards(participant, rewardPoints);
        ordersService.useItem(orders);

        return DoneResponse.builder()
                .rewardedPoints(rewardPoints)
                .build();
    }

    @Override
    public List<ProfileResponse> unmountFrame(User user) {
        List<ProfileResponse> profileResponses = new ArrayList<>();
        List<Orders> frameOrders = ordersService.findAllUsingFrames(user.getId());

        for (Orders frameOrder : frameOrders) {
            ordersService.validateUnmountCondition(frameOrder);
            frameOrder.updateEquipStatus(EquipStatus.AVAILABLE);
            profileResponses.add(ProfileResponse.createByEntity(frameOrder));
        }

        return profileResponses;
    }
}
