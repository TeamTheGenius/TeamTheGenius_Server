package com.genius.gitget.payment.controller;

import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import com.genius.gitget.payment.dto.PaymentFailRequest;
import com.genius.gitget.payment.dto.PaymentRequest;
import com.genius.gitget.payment.dto.PaymentResponse;
import com.genius.gitget.payment.dto.PaymentSuccessRequest;
import com.genius.gitget.payment.dto.PaymentSuccessResponse;
import com.genius.gitget.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    // 1. 프론트가 사용자가 입력한 정보를 [결제하기] 버튼을 통해 /toss 로 결제요청 api 호출
    // 현재 컨트롤러에서 사용자 정보를 저장할 것
    // Request : 사용자 정보
    // Response : DB에 정보 저장 후 사용자 정보 반환
    @PostMapping("/toss")
    public ResponseEntity<SingleResponse<PaymentResponse>> requestTossPayment(
            @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentService.requestTossPayment(paymentRequest.toEntity());
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
