package com.genius.gitget.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class PaymentResponse {
    private Long amount;
    private Long pointAmount;
    private String orderName;
    private String orderId;

    @Builder
    public PaymentResponse(Long amount, Long pointAmount, String orderName, String orderId) {
        this.amount = amount;
        this.pointAmount = pointAmount;
        this.orderName = orderName;
        this.orderId = orderId;

    }
}
