package com.genius.gitget.store.payment.controller;

import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import com.genius.gitget.store.payment.dto.PaymentFailRequest;
import com.genius.gitget.store.payment.dto.PaymentRequest;
import com.genius.gitget.store.payment.dto.PaymentResponse;
import com.genius.gitget.store.payment.dto.PaymentSuccessRequest;
import com.genius.gitget.store.payment.dto.PaymentSuccessResponse;
import com.genius.gitget.store.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/toss")
    public ResponseEntity<SingleResponse<PaymentResponse>> requestTossPayment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentService.requestTossPayment(userPrincipal.getUser(), paymentRequest);
        return ResponseEntity.ok().body(
                new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), paymentResponse)
        );
    }

    @PostMapping("/toss/success")
    public ResponseEntity<SingleResponse<PaymentSuccessResponse>> tossPaymentSuccess(
            @RequestBody PaymentSuccessRequest paymentSuccessRequest) throws Exception {
        PaymentSuccessResponse successResponse = paymentService.tossPaymentSuccess(paymentSuccessRequest);
        return ResponseEntity.ok().body(
                new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), successResponse)
        );
    }

    @PostMapping("/toss/fail")
    public ResponseEntity<CommonResponse> tossPaymentFail(@RequestBody PaymentFailRequest paymentFailRequest) {
        paymentService.tossPaymentFail(paymentFailRequest);
        return ResponseEntity.ok().body(new CommonResponse(
                SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }
}
