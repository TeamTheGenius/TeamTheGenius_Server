package com.genius.gitget.payment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TossPaymentConfig {
    @Value("{payment.toss.test_client_api_key}")
    private String testClientApiKey;

    @Value("{payment.toss.test_secrete_api_key}")
    private String testSecretKey;

    @Value("{payment.toss.success_url}")
    private String successUrl;

    @Value("{payment.toss.fail_url}")
    private String failUrl;

    // 토스페이먼츠에 결제 승인 요청 보낼 url
    public static final String URL = "https://api.tosspayments.com/v1/payments/";
}
