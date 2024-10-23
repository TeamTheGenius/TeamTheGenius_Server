package com.genius.gitget.store.payment.dto;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.store.payment.domain.Payment;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
public class PaymentRequest {
    private Long amount;
    private String orderName;
    private Long pointAmount;
    private String userEmail;

    @Builder
    public PaymentRequest(Long amount, String orderName, Long pointAmount, String userEmail) {
        this.amount = amount;
        this.orderName = orderName;
        this.pointAmount = pointAmount;
        this.userEmail = userEmail;
    }

    public Payment paymentRequestToEntity(User user, PaymentRequest paymentRequest) {
        return Payment.builder()
                .orderId(UUID.randomUUID().toString())
                .amount(paymentRequest.getAmount())
                .orderName(paymentRequest.getOrderName())
                .pointAmount(paymentRequest.getPointAmount())
                .user(user)
                .isSuccess(false)
                .failReason("")
                .build();
    }
}
