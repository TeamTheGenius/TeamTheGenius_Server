package com.genius.gitget.payment.domain;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.domain.Files;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id; // 결제 번호

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 회원 번호 (FK)

    private String merchant_uid; // 결제건의 가맹점 주문번호

    private String name; // 결제건의 제품명

    private Number amount; // 결제건의 결제금액

    private Number cancel_amount; // 결제건의 취소금액

    private String currency; // 결제통화 구분코드

    private String buyer_name; //결제건의 주문자명

    private String status; // 결제건의 결제 상태




    //== Option ==//

    private String pay_method; // 결제건의 결제수단을 구분하는 코드

    private String pg_provider; // 결제건의 PG사 구분코드

    private String pg_tid; // 결제건의 PG사 거래번호

    private String pg_id; // 결제건의 PG사 상점아이디

    //== 연관관계 편의 메서드 ==//
    public void setUser(User user) {
        this.user = user;
    }

}
