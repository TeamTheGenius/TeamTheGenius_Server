package com.genius.gitget.signout;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "signout")
public class Signout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "signout_id")
    private Long id;

    private String identifier;

    private String reason;

    @CreatedDate
    @Column(name = "signout_at", updatable = false)
    private LocalDateTime signoutDate;

    @Builder
    public Signout(String identifier, String reason, LocalDateTime signoutDate) {
        this.identifier = identifier;
        this.reason = reason;
        this.signoutDate = signoutDate;
    }
}
