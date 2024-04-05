package com.genius.gitget.payment.service;

import static com.genius.gitget.store.item.domain.ItemCategory.CERTIFICATION_PASSER;
import static com.genius.gitget.store.item.domain.ItemCategory.POINT_MULTIPLIER;
import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.likes.service.LikesService;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.repository.FilesRepository;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.store.item.service.ItemService;
import com.genius.gitget.store.payment.domain.Payment;
import com.genius.gitget.store.payment.dto.PaymentDetailsResponse;
import com.genius.gitget.store.payment.dto.PaymentRequest;
import com.genius.gitget.store.payment.dto.PaymentResponse;
import com.genius.gitget.store.payment.repository.PaymentRepository;
import com.genius.gitget.store.payment.service.PaymentService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
public class PaymentServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    LikesRepository likesRepository;
    @Autowired
    LikesService likesService;
    @Autowired
    FilesRepository filesRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrdersRepository ordersRepository;


    private User getSavedUser() {
        return userRepository.save(
                User.builder()
                        .role(Role.USER)
                        .nickname("nickname")
                        .providerInfo(ProviderInfo.GITHUB)
                        .identifier("neo5188@gmail.com")
                        .build()
        );
    }

    @ParameterizedTest
    @EnumSource(mode = Mode.EXCLUDE, names = {"PROFILE_FRAME"})
    public void 사용자는_아이템을_구매하고_결제내역을_조회할_수_있다(ItemCategory itemCategory) {
        User user = getSavedUser();
        Item item = getSavedItem(itemCategory);
        getSavedOrder(user, item, itemCategory, 0);
        user.setPoint(1000L);

        ItemResponse itemResponse = itemService.orderItem(user, item.getId());
        assertThat(itemResponse.getItemCategory()).isEqualTo(itemCategory);

        Page<PaymentDetailsResponse> paymentDetails = paymentService.getPaymentDetails(user, PageRequest.of(0, 10));
        List<PaymentDetailsResponse> content = paymentDetails.getContent();

        assertThat(user.getPoint()).isEqualTo(900);
        System.out.println(itemCategory);
        if (itemCategory.equals(CERTIFICATION_PASSER)) {
            assertThat(content.get(0).getOrderName()).isEqualTo("인증 패스권");
        } else if (itemCategory.equals(POINT_MULTIPLIER)) {
            assertThat(content.get(0).getOrderName()).isEqualTo("챌린지 보상 획득 2배 아이템");
        }
        assertThat(content.get(0).getOrderType()).isEqualTo("아이템 구매");
        assertThat(content.get(0).getDecreasedPoint()).isEqualTo("100");
    }

    private Item getSavedItem(ItemCategory itemCategory) {
        return itemRepository.save(Item.builder()
                .itemCategory(itemCategory)
                .cost(100)
                .name(itemCategory.getName())
                .build());
    }

    private Orders getSavedOrder(User user, Item item, ItemCategory itemCategory, int count) {
        Orders orders = Orders.createDefault(count, itemCategory);
        orders.setItem(item);
        orders.setUser(user);
        return ordersRepository.save(orders);
    }

    @Nested
    class 사용자가_결제요청을_할_때 {
        @Test
        public void 존재하지_않는_사용자라면_실패한다() {
            User user = userRepository.save(
                    User.builder()
                            .role(Role.USER)
                            .nickname("nickname")
                            .providerInfo(ProviderInfo.GITHUB)
                            .identifier("kimdozzi")
                            .build()
            );
            Assertions.assertNotNull(user);
            Assertions.assertThrows(BusinessException.class,
                    () -> paymentService.requestTossPayment(user, PaymentRequest.builder()
                            .amount(100L)
                            .orderName("포인트 충전")
                            .userEmail("neo5188@gmail.com")
                            .pointAmount(10L).build()));

        }

        @Test
        public void 결제금액이_100원_미만이면_실패한다() {
            User user = getSavedUser();
            Assertions.assertThrows(BusinessException.class, () ->
                    paymentService.requestTossPayment(user, PaymentRequest.builder()
                            .amount(99L)
                            .orderName("포인트 충전")
                            .userEmail("neo5188@gmail.com")
                            .pointAmount(9L).build()));
        }

        @Test
        public void 정해진_금액이_아닐경우_실패한다() {
            User user = getSavedUser();
            Assertions.assertThrows(BusinessException.class, () ->
                    paymentService.requestTossPayment(user, PaymentRequest.builder()
                            .amount(100L)
                            .orderName("포인트 충전")
                            .userEmail("neo5188@gmail.com")
                            .pointAmount(10L).build()));
        }

        @Test
        public void 최소금액이_100원_이상이고_정해진_금액에_포함된다면_성공한다() {
            User user = getSavedUser();
            PaymentResponse paymentResponse = paymentService.requestTossPayment(user, PaymentRequest.builder()
                    .amount(5000L)
                    .orderName("포인트 충전")
                    .userEmail("neo5188@gmail.com")
                    .pointAmount(500L).build());

            List<Payment> payments = paymentRepository.findPaymentDetailsByUserId(user.getId());

            for (Payment payment : payments) {
                assertThat(payment.getUser().getIdentifier())
                        .isEqualTo(paymentResponse.getUserEmail());
            }
        }
    }
}