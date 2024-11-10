package com.genius.gitget.store.payment.dto;

import com.genius.gitget.store.payment.domain.OrderType;
import com.genius.gitget.store.payment.domain.Payment;
import lombok.Builder;
import lombok.Data;

@Data
public class PaymentDetailsResponse {
    private String orderType;
    private String orderName;
    private String orderLocalDate;
    private String orderDayOfWeek;
    private String increasedPoint;
    private String decreasedPoint;
    private String chargingCash;


    @Builder
    public PaymentDetailsResponse(String orderType, String orderName, String orderLocalDate, String orderDayOfWeek,
                                  String increasedPoint, String decreasedPoint, String chargingCash) {
        this.orderType = orderType;
        this.orderName = orderName;
        this.orderLocalDate = orderLocalDate;
        this.orderDayOfWeek = orderDayOfWeek;
        this.increasedPoint = increasedPoint;
        this.decreasedPoint = decreasedPoint;
        this.chargingCash = chargingCash;
    }

    public static PaymentDetailsResponse createByEntity(Payment payment, String paymentDateFormat,
                                                        String dayOfWeekKorean) {
        return PaymentDetailsResponse.builder()
                .orderType(String.valueOf(payment.getOrderType().getValue()))
                .orderName(payment.getOrderName())
                .orderLocalDate(paymentDateFormat)
                .orderDayOfWeek(dayOfWeekKorean)
                .decreasedPoint(
                        payment.getOrderType().equals(OrderType.ITEM) ? String.valueOf(payment.getPointAmount()) : null)
                .increasedPoint(
                        payment.getOrderType().equals(OrderType.POINT) ? String.valueOf(payment.getPointAmount())
                                : null)
                .chargingCash(
                        payment.getOrderType().equals(OrderType.POINT) ? String.valueOf(payment.getAmount()) : null)
                .build();
    }
}
