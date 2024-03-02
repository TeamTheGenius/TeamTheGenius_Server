package com.genius.gitget.payment.dto;

import lombok.Data;

@Data
public class PaymentSuccessRequest {
    private String orderId;
    private String paymentKey;
    private String amount;
}
