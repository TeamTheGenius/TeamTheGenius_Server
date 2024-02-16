package com.genius.gitget.payment.dto;

import com.genius.gitget.payment.domain.PayType;
import com.genius.gitget.payment.domain.Payment;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
public class PaymentRequest {
    private PayType payType;
    private Long amount; // 가격 정보
    private String orderName; // 주문명
    private String successRedirectUrl; // 성공 시 리다이렉트 될 URL
    private String failRedirectUrl; // 실패 시 리다이렉트 될 URL

    @Builder
    public PaymentRequest(PayType payType, Long amount, String orderName, String successRedirectUrl,
                          String failRedirectUrl) {
        this.payType = payType;
        this.amount = amount;
        this.orderName = orderName;
        this.successRedirectUrl = successRedirectUrl;
        this.failRedirectUrl = failRedirectUrl;
    }

    public Payment toEntity() {
        return Payment.builder()
                .payType(payType)
                .amount(amount)
                .orderName(orderName)
                .orderId(UUID.randomUUID().toString())
                .isSuccess(false)
                .build();
    }
}
