package com.genius.gitget.payment.dto;

import lombok.Data;

@Data
public class PaymentFailRequest {
    private String message;
    private String orderId;
}