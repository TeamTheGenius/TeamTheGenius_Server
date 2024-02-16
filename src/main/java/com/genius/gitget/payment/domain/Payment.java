package com.genius.gitget.payment.domain;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.payment.dto.PaymentResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id; // 결제 번호

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 회원 번호 (FK)

    private String orderId;
    private String paymentKey;
    private Long amount;

    private String orderName;
    private String CancelReason;
    private String failReason;
    private boolean isSuccess;
    private boolean isCancel;
    @Enumerated(EnumType.STRING)
    private PayType payType;

    // TODO 생성일 처리
    private LocalDateTime createdAt;


    @Builder
    public Payment(String orderId, String paymentKey, Long amount, String orderName, String cancelReason,
                   String failReason,
                   boolean isSuccess, boolean isCancel, PayType payType) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderName = orderName;
        CancelReason = cancelReason;
        this.failReason = failReason;
        this.isSuccess = isSuccess;
        this.isCancel = isCancel;
        this.payType = payType;
    }

    public PaymentResponse paymentResponse() { // DB에 저장하게 될 결제 관련 정보들
        return PaymentResponse.builder()
                .payType(payType.name())
                .amount(amount)
                .orderName(orderName)
                .orderId(orderId)
                //.customerEmail(customer.getEmail())
                //.customerName(customer.getName())
                //.createdAt(String.valueOf(getCreatedAt()))
                .isCancel(isCancel)
                .failReason(failReason)
                .build();
    }

    //== 연관관계 편의 메서드 ==//
    public void setUser(User user) {
        this.user = user;
    }

}
