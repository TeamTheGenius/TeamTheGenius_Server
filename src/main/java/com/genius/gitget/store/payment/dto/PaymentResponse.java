package com.genius.gitget.store.payment.dto;

import com.genius.gitget.store.payment.domain.Payment;
import lombok.Builder;
import lombok.Data;

@Data
public class PaymentResponse {
    private Long amount;
    private Long pointAmount;
    private String orderName;
    private String orderId;
    private String userEmail;

    @Builder
    public PaymentResponse(Long amount, Long pointAmount, String orderName, String orderId, String userEmail) {
        this.amount = amount;
        this.pointAmount = pointAmount;
        this.orderName = orderName;
        this.orderId = orderId;
        this.userEmail = userEmail;
    }

    public static PaymentResponse createByEntity(Payment payment) {

        return PaymentResponse.builder()
                .amount(payment.getAmount())
                .pointAmount(payment.getPointAmount())
                .orderName(payment.getOrderName())
                .orderId(payment.getOrderId())
                .userEmail(payment.getUser().getIdentifier())
                .build();
    }
}
