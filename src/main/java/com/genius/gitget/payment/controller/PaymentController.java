package com.genius.gitget.payment.controller;

import com.genius.gitget.payment.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/payment")
public class PaymentController {
    // 토큰 발급을 위해 아임포트에서 제공해주는 rest api
    private IamportClient iamportClient;

    @Value("${imp.api.key}")
    private String apiKey;
    @Value("${imp.api.secretkey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    private final PaymentService paymentService;

    // 결제 검증
    @GetMapping("/verify/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable(value = "imp_uid") String imp_uid) throws IamportResponseException, IOException
    {
        return iamportClient.paymentByImpUid(imp_uid);
    }
}
