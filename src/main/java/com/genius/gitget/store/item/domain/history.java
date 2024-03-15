package com.genius.gitget.store.item.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Null;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "history")
public class history {
    @Id
    @GeneratedValue
    @Column(name = "history_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PurchaseType purchaseType;

    @Null
    private String PurchaseItem;

    private LocalDateTime PurchaseDate;


}
