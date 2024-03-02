package com.genius.gitget.payment.dto;

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
}
