package com.genius.gitget.payment.dto;

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
}
