package com.genius.gitget.payment.service;

import com.genius.gitget.payment.domain.Payment;
import com.genius.gitget.payment.dto.PaymentResponse;
import com.genius.gitget.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse requestTossPayment(Payment payment) {
        return paymentRepository.save(payment).paymentResponse();
    }
}
