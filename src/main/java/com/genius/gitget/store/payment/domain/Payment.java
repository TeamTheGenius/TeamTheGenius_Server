package com.genius.gitget.store.payment.domain;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String orderId;

    private String paymentKey;

    private Long amount;

    private Long pointAmount;

    private String orderName;

    private boolean isSuccess;

    private String failReason;

    @Column(name = "success_at", updatable = false)
    private LocalDateTime successDate;

    @Builder
    public Payment(String orderId, String paymentKey, Long amount, Long pointAmount, String orderName,
                   boolean isSuccess, String failReason, User user) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.pointAmount = pointAmount;
        this.orderName = orderName;
        this.isSuccess = isSuccess;
        this.failReason = failReason;
        this.user = user;
    }

    public void setPaymentSuccessStatus(String paymentKey, boolean isSuccess) {
        this.paymentKey = paymentKey;
        this.isSuccess = isSuccess;
    }

    public void setPaymentFailStatus(String message, boolean isSuccess) {
        this.failReason = message;
        this.isSuccess = isSuccess;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
