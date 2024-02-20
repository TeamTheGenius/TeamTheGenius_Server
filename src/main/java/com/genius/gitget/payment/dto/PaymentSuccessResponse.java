package com.genius.gitget.payment.dto;


import lombok.Builder;
import lombok.Data;

@Data
public class PaymentSuccessResponse {

    private String orderId;
    private String paymentKey;
    private Long amount;
    private Long pointAmount;
    private String orderName;
    private boolean isSuccess;
    private String failReason;

    @Builder
    public PaymentSuccessResponse(String orderId, String paymentKey, Long amount, Long pointAmount, String orderName,
                                  boolean isSuccess, String failReason) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.pointAmount = pointAmount;
        this.orderName = orderName;
        this.isSuccess = isSuccess;
        this.failReason = failReason;
    }
}
