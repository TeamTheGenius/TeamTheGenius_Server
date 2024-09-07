package com.genius.gitget.store.payment.dto;

import lombok.Data;

@Data
public class PaymentFailRequest {
    private String message;
    private String orderId;
}