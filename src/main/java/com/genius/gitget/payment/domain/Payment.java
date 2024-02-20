package com.genius.gitget.payment.domain;

import com.genius.gitget.payment.dto.PaymentResponse;
import com.genius.gitget.payment.dto.PaymentSuccessResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "payment")
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private String orderId;

    private String paymentKey;

    private Long amount;

    private Long pointAmount;

    private String orderName;

    private boolean isSuccess;

    private String failReason;

    @Builder
    public Payment(String orderId, String paymentKey, Long amount, Long pointAmount, String orderName,
                   boolean isSuccess, String failReason) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.pointAmount = pointAmount;
        this.orderName = orderName;
        this.isSuccess = isSuccess;
        this.failReason = failReason;
    }

    public PaymentResponse paymentResponse() {
        return PaymentResponse.builder()
                .amount(amount)
                .pointAmount(pointAmount)
                .orderName(orderName)
                .orderId(orderId)
                .build();
    }

    public PaymentSuccessResponse paymentSuccessResponse() {
        return PaymentSuccessResponse.builder()
                .orderId(orderId)
                .paymentKey(paymentKey)
                .amount(amount)
                .pointAmount(pointAmount)
                .orderName(orderName)
                .isSuccess(isSuccess)
                .failReason(failReason)
                .build();
    }

    public void setPaymentSuccessStatus(String paymentKey, boolean isSuccess) {
        this.paymentKey = paymentKey;
        this.isSuccess = isSuccess;
    }

    public void setPaymentFailStatus(String message, boolean isSuccess) {
        this.failReason = message;
        this.isSuccess = isSuccess;
    }
}
