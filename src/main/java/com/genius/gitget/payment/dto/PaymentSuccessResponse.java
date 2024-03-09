package com.genius.gitget.payment.dto;


import com.genius.gitget.payment.domain.Payment;
import lombok.Builder;
import lombok.Data;

@Data
public class PaymentSuccessResponse {

    private String orderId;
    private String paymentKey;
    private Long amount;
    private Long pointAmount;
    private String orderName;
    private String isSuccess;
    private String failReason;

    @Builder
    public PaymentSuccessResponse(String orderId, String paymentKey, Long amount, Long pointAmount, String orderName,
                                  boolean isSuccess, String failReason) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.pointAmount = pointAmount;
        this.orderName = orderName;
        this.isSuccess = String.valueOf(isSuccess);
        this.failReason = failReason;
    }

    public static PaymentSuccessResponse createByEntity(Payment payment) {
        return PaymentSuccessResponse.builder()
                .paymentKey(payment.getPaymentKey())
                .amount(payment.getAmount())
                .orderName(payment.getOrderName())
                .pointAmount(payment.getPointAmount())
                .orderId(payment.getOrderId())
                .isSuccess(payment.isSuccess())
                .build();
    }
}
