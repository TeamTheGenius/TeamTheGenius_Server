package com.genius.gitget.store.payment.controller;

import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.exception.SuccessCode;
import com.genius.gitget.global.util.response.dto.PagingResponse;
import com.genius.gitget.store.payment.dto.PaymentDetailsResponse;
import com.genius.gitget.store.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<PagingResponse<PaymentDetailsResponse>> getPaymentDetails(@AuthenticationPrincipal
                                                                                    UserPrincipal userPrincipal,
                                                                                    @PageableDefault
                                                                                    Pageable pageable) {

        Page<PaymentDetailsResponse> paymentDetails = paymentService.getPaymentDetails(userPrincipal.getUser(),
                pageable);

        return ResponseEntity.ok().body(
                new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), paymentDetails)
        );
    }
}
